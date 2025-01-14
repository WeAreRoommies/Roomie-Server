package server.producer.domain.service;

import org.springframework.stereotype.Service;
import server.producer.domain.dto.HouseInfoDto;
import server.producer.domain.dto.response.HouseDetailsResponseDto;
import server.producer.domain.repository.HouseRepository;
import server.producer.entity.House;

@Service
public class HouseService {
    private final HouseRepository houseRepository;

    public HouseService(HouseRepository houseRepository) {
        this.houseRepository = houseRepository;
    }

    public HouseDetailsResponseDto getHouseDetails(final Long houseId) {
        House selectedHouse = houseRepository.findById(houseId)
                .orElseThrow(()->new IllegalArgumentException("해당 House를 찾을 수 없습니다."));
        HouseInfoDto houseInfoDto = HouseInfoDto.builder()
                .houseId(houseId)
                .name(selectedHouse.getName())
                .

    }
}
