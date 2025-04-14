package server.producer.domain.controller;

import entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import server.producer.common.dto.ApiResponseDto;
import server.producer.common.dto.enums.ErrorCode;
import server.producer.common.dto.enums.SuccessCode;
import server.producer.domain.dto.request.SocialLoginRequestDto;
import server.producer.domain.dto.request.SocialSignupRequestDto;
import server.producer.domain.dto.response.SocialLoginResponseDto;
import server.producer.domain.dto.response.SocialSignupResponseDto;
import server.producer.domain.repository.UserRepository;
import server.producer.domain.service.SocialLoginService;
import server.producer.security.jwt.JwtTokenProvider;
import server.producer.security.jwt.RefreshTokenRepository;

@RestController
@RequestMapping("v1/auth")
@RequiredArgsConstructor
public class AuthController {
	private final SocialLoginService socialLoginService;
	private final RefreshTokenRepository refreshTokenRepository;
	private final UserRepository userRepository;
	private final JwtTokenProvider jwtTokenProvider;

	@PostMapping("/oauth/login")
	public ApiResponseDto<SocialLoginResponseDto> socialLogin(@RequestBody SocialLoginRequestDto request) {
		try {
			SocialLoginResponseDto responseDto = socialLoginService.login(request.getProvider(), request.getAccessToken());
			return ApiResponseDto.success(SuccessCode.SOCIAL_LOGIN_SUCCESS, responseDto);
		} catch (Exception e) {
			return ApiResponseDto.fail(ErrorCode.NEED_SOCIAL_SIGNUP);
		}
	}

	@PostMapping("/oauth/signup")
	public ApiResponseDto<SocialSignupResponseDto> signup(@RequestBody SocialSignupRequestDto request) {
		try {
			SocialSignupResponseDto response = socialLoginService.signup(
					request.getProvider(),
					request.getAccessToken(),
					request.getNickname()
			);
			return ApiResponseDto.success(SuccessCode.SOCIAL_SIGNUP_SUCCESS, response);
		} catch (Exception e) {
			return ApiResponseDto.fail(ErrorCode.UNAUTHORIZED_SOCIAL_TOKEN);
		}
	}

	@PostMapping("/oauth/reissue")
	public ApiResponseDto<SocialLoginResponseDto> reissue(@RequestHeader("Refresh-Token") String refreshToken) {
		try {
			Long userId = refreshTokenRepository.findUserIdByToken(refreshToken)
					.orElseThrow(() -> new RuntimeException("유효하지 않은 리프레시 토큰입니다."));

			User user = userRepository.findById(userId)
					.orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

			String newAccess = jwtTokenProvider.createToken(user);

			return ApiResponseDto.success(SuccessCode.TOKEN_REISSUE_SUCCESS,
					SocialLoginResponseDto.builder()
							.accessToken(newAccess)
							.refreshToken(refreshToken) // 기존 refresh 유지
							.build());
		} catch (Exception e) {
			return ApiResponseDto.fail(ErrorCode.UNAUTHORIZED_SOCIAL_TOKEN);
		}
	}

}
