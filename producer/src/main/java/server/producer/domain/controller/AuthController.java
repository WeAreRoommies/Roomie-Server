package server.producer.domain.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.producer.common.dto.ApiResponseDto;
import server.producer.common.dto.enums.ErrorCode;
import server.producer.common.dto.enums.SuccessCode;
import server.producer.domain.dto.request.SocialLoginRequestDto;
import server.producer.domain.dto.request.SocialSignupRequestDto;
import server.producer.domain.dto.response.SocialLoginResponseDto;
import server.producer.domain.dto.response.SocialSignupResponseDto;
import server.producer.domain.service.SocialLoginService;

@RestController
@RequestMapping("v1/auth")
@RequiredArgsConstructor
public class AuthController {
	private final SocialLoginService socialLoginService;

	@PostMapping("/oauth/login")
	public ApiResponseDto<SocialLoginResponseDto> socialLogin(@RequestBody SocialLoginRequestDto request) {
		try {
			String token = socialLoginService.login(request.getProvider(), request.getAccessToken());
			SocialLoginResponseDto responseDto = SocialLoginResponseDto.builder().accessToken(token).build();
			return ApiResponseDto.success(SuccessCode.SOCIAL_LOGIN_SUCCESS, responseDto);
		} catch (Exception e) {
			return ApiResponseDto.fail(ErrorCode.NEED_SOCIAL_SIGNUP);
		}
	}

	@PostMapping("/oauth/signup")
	public ApiResponseDto<SocialSignupResponseDto> signup(@RequestBody SocialSignupRequestDto request) {
		try {
		String token = socialLoginService.signup(request.getProvider(), request.getAccessToken(), request.getNickname());
		SocialSignupResponseDto responseDto = SocialSignupResponseDto.builder().accessToken(token).build();
		return ApiResponseDto.success(SuccessCode.SOCIAL_SIGNUP_SUCCESS, responseDto);
		} catch (Exception e) {
			return ApiResponseDto.fail(ErrorCode.UNAUTHORIZED_SOCIAL_TOKEN);
		}
	}
}
