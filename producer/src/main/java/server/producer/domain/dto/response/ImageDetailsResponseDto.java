package server.producer.domain.dto.response;

import lombok.Builder;

import java.awt.*;
import java.util.List;

public record ImageDetailsResponseDto(
        Images images
) {
    @Builder
    public ImageDetailsResponseDto{
    }
    public record Images(
            String mainImgUrl,
            String mainImgDescription,
            List<String> facilityImgUrls,
            String facilityImgDescription,
            String floorImgUrl
    ){
        @Builder
        public Images{
        }
    }
}
