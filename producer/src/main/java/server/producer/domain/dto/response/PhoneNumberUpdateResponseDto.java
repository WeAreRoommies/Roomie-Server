package server.producer.domain.dto.response;

import lombok.Builder;

public record PhoneNumberUpdateResponseDto(
        String phoneNumber    // 연락처 (예: 010-1234-5678)
) {
    @Builder
    public PhoneNumberUpdateResponseDto {}
}

