package server.producer.domain.dto.response;

import lombok.Builder;
import org.springframework.transaction.annotation.Transactional;

public record NameUpdateResponseDto(
        String name
) {
    @Builder
    public NameUpdateResponseDto {}
}
