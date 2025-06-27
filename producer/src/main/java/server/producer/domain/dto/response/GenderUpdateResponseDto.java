package server.producer.domain.dto.response;

import entity.Gender;
import lombok.Builder;

public record GenderUpdateResponseDto(
        Gender gender         // 성별 (예: MALE / FEMALE)
) {
    @Builder
    public GenderUpdateResponseDto {}
}
