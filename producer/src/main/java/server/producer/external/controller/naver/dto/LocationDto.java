package server.producer.external.controller.naver.dto;

public record LocationDto(
        double longitude,
        double latitude,
        String location,
        String address,
        String roadAddress
) {

}
