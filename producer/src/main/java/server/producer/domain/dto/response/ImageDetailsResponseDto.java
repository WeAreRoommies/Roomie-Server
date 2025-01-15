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
            String mainImgUrls,
            String mainImgDescription,
            List<String> facilityImgUrls,
            String facilityImgDescription,
            String floorImgUrls
    ){
        @Builder
        public Images{
        }
    }
}
