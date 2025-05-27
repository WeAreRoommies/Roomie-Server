package server.producer.domain.dto.request;

import java.time.LocalDate;

public record BirthDayUpdateRequestDto(
        LocalDate birthDay   // 생년월일 (yyyy-MM-dd)
) {}
