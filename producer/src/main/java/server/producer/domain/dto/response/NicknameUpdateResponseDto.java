package server.producer.domain.dto.response;

import lombok.Builder;

public record NicknameUpdateResponseDto(
        String nickname
) {
    @Builder
    public NicknameUpdateResponseDto {}
}
