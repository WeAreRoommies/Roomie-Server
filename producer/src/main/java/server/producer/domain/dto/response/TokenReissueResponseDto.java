package server.producer.domain.dto.response;

import lombok.Builder;

public record TokenReissueResponseDto(
        String accessToken
) {
    @Builder
    public TokenReissueResponseDto {
    }
}
