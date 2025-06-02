package server.producer.domain.controller;

import entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.producer.common.dto.ApiResponseDto;
import server.producer.common.dto.enums.ErrorCode;
import server.producer.common.dto.enums.SuccessCode;
import server.producer.domain.dto.request.SocialLoginRequestDto;
import server.producer.domain.dto.request.SocialSignupRequestDto;
import server.producer.domain.dto.response.SocialLoginResponseDto;
import server.producer.domain.dto.response.SocialSignupResponseDto;
import server.producer.domain.dto.response.TokenReissueResponseDto;
import server.producer.domain.repository.UserRepository;
import server.producer.domain.service.SocialLoginService;
import server.producer.security.jwt.JwtTokenProvider;
import server.producer.security.jwt.RefreshTokenRepository;

@Slf4j
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
			SocialLoginResponseDto responseDto = socialLoginService.loginOrSignup(
					request.getProvider(),
					request.getAccessToken()
			);
			return ApiResponseDto.success(SuccessCode.SOCIAL_LOGIN_SUCCESS, responseDto);
		} catch (Exception e) {
			log.error("[카카오 사용자 정보 조회 실패] accessToken: {}", request.getAccessToken(), e);
			return ApiResponseDto.fail(ErrorCode.UNAUTHORIZED_SOCIAL_TOKEN);
		}
	}

	@PostMapping("/oauth/reissue")
	public ApiResponseDto<TokenReissueResponseDto> reissue(@RequestHeader("Authorization") String refreshTokenHeader) {
		if (refreshTokenHeader == null || !refreshTokenHeader.startsWith("Bearer ")) {
			return ApiResponseDto.fail(ErrorCode.MISSING_REQUIRED_HEADER);
		}

		String refreshToken = refreshTokenHeader.substring(7);

		try {
			Long userId = refreshTokenRepository.findUserIdByToken(refreshToken)
					.orElseThrow(() -> new RuntimeException("유효하지 않은 RefreshToken"));

			User user = userRepository.findById(userId)
					.orElseThrow(() -> new RuntimeException("유저 없음"));

			String newAccess = jwtTokenProvider.createToken(user);
			TokenReissueResponseDto responseDto = new TokenReissueResponseDto(newAccess);

			return ApiResponseDto.success(SuccessCode.TOKEN_REISSUE_SUCCESS, responseDto);
		} catch (Exception e) {
			return ApiResponseDto.fail(ErrorCode.UNAUTHORIZED_SOCIAL_TOKEN);
		}
	}
}
