package server.producer.domain.dto.request;

public record SocialLoginRequestDto (
		String provider,
		String accessToken
) {}