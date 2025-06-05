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

		// 1. 형식 및 만료 검증
		if (!jwtTokenProvider.validateToken(refreshToken)) {
			log.warn("[리프레시 토큰 오류] 토큰 만료 또는 포맷 문제 발생. 토큰: {}", refreshToken);
			return ApiResponseDto.fail(ErrorCode.INVALID_REFRESH_TOKEN);
		}

		// 2. 저장소에서 userId 매핑 조회
		Long userId = refreshTokenRepository.findUserIdByToken(refreshToken)
				.orElse(null);

		if (userId == null) {
			log.warn("[리프레시 토큰 오류] 저장소에서 userId 찾기 실패. 토큰: {}", refreshToken);
			return ApiResponseDto.fail(ErrorCode.INVALID_REFRESH_TOKEN);
		}

		// 3. 유저 조회
		User user = userRepository.findById(userId)
				.orElse(null);

		if (user == null) {
			log.warn("[리프레시 토큰 오류] userId는 존재하나 사용자 정보 없음. userId: {}", userId);
			return ApiResponseDto.fail(ErrorCode.USER_NOT_FOUND_FOR_REFRESH);
		}

		// 4. accessToken 재발급
		String newAccess = jwtTokenProvider.createToken(user);
		TokenReissueResponseDto responseDto = new TokenReissueResponseDto(newAccess);

		return ApiResponseDto.success(SuccessCode.TOKEN_REISSUE_SUCCESS, responseDto);
	}

	@DeleteMapping("/logout")
	public ApiResponseDto<Void> logout(@RequestHeader("Authorization") String refreshTokenHeader) {
		if (refreshTokenHeader == null || !refreshTokenHeader.startsWith("Bearer ")) {
			return ApiResponseDto.fail(ErrorCode.MISSING_REQUIRED_HEADER);
		}

		String refreshToken = refreshTokenHeader.substring(7);

		// 1. 리프레시 토큰 형식 및 만료 검증
		if (!jwtTokenProvider.validateToken(refreshToken)) {
			log.warn("[로그아웃 실패] 유효하지 않은 리프레시 토큰. 토큰: {}", refreshToken);
			return ApiResponseDto.fail(ErrorCode.INVALID_REFRESH_TOKEN);
		}

		// 2. 저장소에서 userId 매핑 조회
		Long userId = refreshTokenRepository.findUserIdByToken(refreshToken)
				.orElse(null);

		if (userId == null) {
			log.warn("[로그아웃 실패] 저장소에서 userId 찾기 실패. 토큰: {}", refreshToken);
			return ApiResponseDto.fail(ErrorCode.INVALID_REFRESH_TOKEN);
		}

		// 3. 리프레시 토큰 삭제
		try {
			refreshTokenRepository.deleteByToken(refreshToken);
			log.info("[로그아웃 성공] userId: {}", userId);
			return ApiResponseDto.success(SuccessCode.LOGOUT_SUCCESS, null);
		} catch (Exception e) {
			log.error("[로그아웃 실패] 리프레시 토큰 삭제 중 오류 발생. userId: {}, 토큰: {}", userId, refreshToken, e);
			return ApiResponseDto.fail(ErrorCode.LOGOUT_FAILED);
		}
	}
}
