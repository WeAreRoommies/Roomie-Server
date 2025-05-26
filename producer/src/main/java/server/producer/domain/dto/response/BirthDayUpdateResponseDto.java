package server.producer.domain.dto.response;

import java.time.LocalDate;

public record BirthDayUpdateResponseDto(
        LocalDate birthDay   // 생년월일 (yyyy-MM-dd)
) {}
