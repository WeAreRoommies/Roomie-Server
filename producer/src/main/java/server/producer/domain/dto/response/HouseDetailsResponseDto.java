package server.producer.domain.dto.response;

import lombok.Builder;
import lombok.Data;
import server.producer.domain.dto.HouseInfoDto;
import server.producer.domain.dto.RoomDto;
import server.producer.domain.dto.RoommateDto;

import java.util.List;

@Data
@Builder
public class HouseDetailsResponseDto {
    private HouseInfoDto houseInfo;
    private List<RoomDto> rooms;
    private List<RoommateDto> roommates;
}
