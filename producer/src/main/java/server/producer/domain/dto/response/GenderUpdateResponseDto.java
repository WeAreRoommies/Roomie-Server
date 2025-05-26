package server.producer.domain.dto.response;

import entity.Gender;

public record GenderUpdateResponseDto(
        Gender gender         // 성별 (예: MALE / FEMALE)
) {}
