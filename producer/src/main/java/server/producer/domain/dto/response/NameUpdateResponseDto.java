package server.producer.domain.dto.response;

import lombok.Builder;

public record NameUpdateResponseDto(
        String name
) {
    @Builder
    public NameUpdateResponseDto {}
}
