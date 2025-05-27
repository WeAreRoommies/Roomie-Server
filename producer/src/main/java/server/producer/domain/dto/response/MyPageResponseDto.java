package server.producer.domain.dto.response;

import entity.SocialType;

public record MyPageResponseDto(
        String nickname,
        SocialType socialType
) {}