package server.producer.domain.dto.response;

import lombok.Builder;

@Builder
public record SocialReissueResponseDto(
        String accessToken,
        String refreshToken
) {}

