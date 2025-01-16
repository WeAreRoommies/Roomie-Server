package server.producer.domain.dto.response;

import lombok.Builder;

import java.util.List;

public record RoomDetailsResponseDto(
        List<Room> rooms
) {
    @Builder
    public RoomDetailsResponseDto{
    }
    public record Room(
            Long roomId,
            String name,
            List<String> facility,
            Boolean status,
            List<String> mainImageUrl
    ){
        @Builder
        public Room{
        }
    }
}
