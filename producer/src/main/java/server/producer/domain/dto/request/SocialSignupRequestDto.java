package server.producer.domain.dto.request;

public record SocialSignupRequestDto(
		String provider,
		String accessToken,
		String nickname
) {
}
