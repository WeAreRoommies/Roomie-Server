package server.producer.domain.controller;

import entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.producer.common.dto.ApiResponseDto;
import server.producer.common.dto.enums.ErrorCode;
import server.producer.common.dto.enums.SuccessCode;
import server.producer.domain.dto.request.SocialLoginRequestDto;
import server.producer.domain.dto.request.SocialSignupRequestDto;
import server.producer.domain.dto.response.SocialLoginResponseDto;
import server.producer.domain.dto.response.SocialReissueResponseDto;
import server.producer.domain.dto.response.SocialSignupResponseDto;
import server.producer.domain.repository.UserRepository;
import server.producer.domain.service.SocialLoginService;
import server.producer.security.jwt.JwtTokenProvider;
import server.producer.security.jwt.RefreshTokenRepository;

import java.util.Optional;

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
	public ApiResponseDto<SocialReissueResponseDto> reissue(@RequestHeader("Authorization") String refreshTokenHeader) {
		// 1. 헤더 유효성 검사
		if (refreshTokenHeader == null || !refreshTokenHeader.startsWith("Bearer ")) {
			return ApiResponseDto.fail(ErrorCode.MISSING_REQUIRED_PARAMETER);
		}

		String refreshToken = refreshTokenHeader.substring(7);

		// 2. Redis에서 userId 조회
		Optional<Long> optionalUserId = refreshTokenRepository.findUserIdByToken(refreshToken);
		if (optionalUserId.isEmpty()) {
			return ApiResponseDto.fail(ErrorCode.UNAUTHORIZED_SOCIAL_TOKEN); // 유효하지 않은 RefreshToken
		}
		Long userId = optionalUserId.get();

		// 3. DB에서 User 조회
		Optional<User> optionalUser = userRepository.findById(userId);
		if (optionalUser.isEmpty()) {
			return ApiResponseDto.fail(ErrorCode.USER_NOT_FOUND); // 유저 없음
		}
		User user = optionalUser.get();

		// 4. AccessToken 재발급
		String newAccess = jwtTokenProvider.createToken(user);

		SocialReissueResponseDto responseDto = SocialReissueResponseDto.builder()
				.accessToken(newAccess)
				.refreshToken(refreshToken)
				.build();

		return ApiResponseDto.success(SuccessCode.TOKEN_REISSUE_SUCCESS, responseDto);
	}
}
