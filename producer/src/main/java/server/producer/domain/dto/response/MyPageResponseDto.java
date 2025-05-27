package server.producer.domain.dto.response;

import entity.SocialType;
import lombok.Builder;

public record MyPageResponseDto(
        String nickname,
        SocialType socialType
)  {
    @Builder
    public MyPageResponseDto {
    }
}