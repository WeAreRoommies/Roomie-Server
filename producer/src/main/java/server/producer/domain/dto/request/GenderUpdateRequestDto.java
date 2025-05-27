package server.producer.domain.dto.request;

import entity.Gender;

public record GenderUpdateRequestDto(
        Gender gender         // 성별 (예: MALE / FEMALE)
) {}
