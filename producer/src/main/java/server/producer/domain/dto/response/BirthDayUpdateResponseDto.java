package server.producer.domain.dto.response;

import lombok.Builder;

import java.time.LocalDate;

public record BirthDayUpdateResponseDto(
        LocalDate birthDay   // 생년월일 (yyyy-MM-dd)
) {
    @Builder
    public BirthDayUpdateResponseDto {}
}
