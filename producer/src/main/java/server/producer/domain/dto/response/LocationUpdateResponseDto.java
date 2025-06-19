package server.producer.domain.dto.response;

import lombok.Builder;

@Builder
public record LocationUpdateResponseDto(
        double latitude,
        double longitude,
        String location
) {
    public LocationUpdateResponseDto() {
        this(0.0, 0.0, "");
    }
}