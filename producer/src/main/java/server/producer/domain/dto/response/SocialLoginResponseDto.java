package server.producer.domain.dto.response;

import lombok.Builder;

@Builder
public record SocialLoginResponseDto(
		String accessToken,
		String refreshToken
) {}