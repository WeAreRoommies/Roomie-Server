package server.producer.external.controller.naver.dto;

import java.util.List;

public record LocationsDto(
        List<LocationDto> locations) {

    public static LocationsDto of(List<LocationDto> locations) {
        return new LocationsDto(locations);
    }
}
