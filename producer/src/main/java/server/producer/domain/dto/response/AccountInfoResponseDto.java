package server.producer.domain.dto.response;

import entity.Gender;
import entity.SocialType;
import lombok.Builder;

import java.time.LocalDate;

public record AccountInfoResponseDto(
        String nickname,
        SocialType socialType,
        String name,
        LocalDate birthDate,
        String phoneNumber,
        Gender gender
) {
    @Builder
    public AccountInfoResponseDto {}
}
