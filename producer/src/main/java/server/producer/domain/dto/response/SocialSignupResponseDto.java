package server.producer.domain.dto.response;

import lombok.Builder;

@Builder
public record SocialSignupResponseDto(
		String accessToken,
		String refreshToken
) {
}
