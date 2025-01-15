package server.producer.domain.dto;

import lombok.Builder;

public record MyPageDto (
    String name
) {
    @Builder
    public MyPageDto {

    }
}
