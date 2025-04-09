package server.producer.domain.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.producer.common.dto.ApiResponseDto;
import server.producer.domain.service.SocialLoginService;

@RestController
@RequestMapping("v1/auth")
@RequiredArgsConstructor
public class AuthController {
	private final SocialLoginService socialLoginService;

	@PostMapping("/oauth/login")
	public ApiResponseDto<> socialLogin(@RequestBody SocialLoginRequest request) {
		String token = loginService.login(request.getProvider(), request.getAccessToken());
		return ApiResponseDto.k(Collections.singletonMap("accessToken", token));
	}

	@PostMapping("/oauth/signup")
	public ApiResponseDto<> signup(@RequestBody SocialSignupRequest request) {
		String token = loginService.signup(request.getProvider(), request.getAccessToken(), request.getNickname());
		return ResponseEntity.ok(Collections.singletonMap("accessToken", token));
	}
}
