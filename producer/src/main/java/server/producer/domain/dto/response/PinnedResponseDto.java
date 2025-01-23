package server.producer.domain.dto.response;

import lombok.Builder;

public record PinnedResponseDto (
        boolean isPinned
){
    @Builder
    public PinnedResponseDto {
    }
}
