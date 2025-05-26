package server.producer.domain.dto.response;

import lombok.Builder;

public record MyPageResponseDto(
        String name
) {
    @Builder
    public MyPageResponseDto {
    }
}
