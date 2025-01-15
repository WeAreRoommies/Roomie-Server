package server.producer.domain.service;

import org.springframework.stereotype.Service;
import server.producer.domain.RoomStatistics;
import server.producer.domain.dto.HouseInfoDto;
import server.producer.domain.dto.RoomDto;
import server.producer.domain.dto.RoommateDto;
import server.producer.domain.dto.response.HouseDetailsResponseDto;
import server.producer.domain.repository.HouseRepository;
import server.producer.entity.House;
import server.producer.entity.Room;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HouseService {
    private final HouseRepository houseRepository;

    public HouseService(HouseRepository houseRepository) {
        this.houseRepository = houseRepository;
    }

    public HouseDetailsResponseDto getHouseDetails(final Long houseId, final Long userId) {
        House selectedHouse = houseRepository.findById(houseId)
                .orElseThrow(()->new IllegalArgumentException("해당 House를 찾을 수 없습니다."));
        List<Room> rooms = selectedHouse.getRooms();
        final String monthlyRent = RoomStatistics.calculateMonthlyRent(rooms);
        final String deposit = RoomStatistics.calculateDeposit(rooms);
        final String occupancyType = RoomStatistics.calculateOccupancyType(rooms);
        final String occupancyStatus = RoomStatistics.calculateOccupancyStatus(rooms);
        final List<String> moodTags = RoomStatistics.mergeTags(selectedHouse);
        final List<String> groundRules = Arrays.stream(selectedHouse.getGroundRule().split("#")).toList();
        final List<String> safetyLivingFacilities = Arrays.stream(selectedHouse.getSafetyLivingFacility().split("#")).toList();
        final List<String> kitchenFacilities = Arrays.stream(selectedHouse.getKitchenFacility().split("#")).toList();

        final boolean isPinned = selectedHouse.getPins().stream()
                .anyMatch(pin -> pin.getUser().getId().equals(userId));
        HouseInfoDto houseInfoDto = HouseInfoDto.builder()
                .houseId(houseId)
                .name(selectedHouse.getName())
                .mainImgUrl(selectedHouse.getMainImgUrl())
                .monthlyRent(monthlyRent)
                .deposit(deposit)
                .location(selectedHouse.getLocation())
                .occupancyTypes(occupancyType)
                .occupancyStatus(occupancyStatus)
                .genderPolicy(selectedHouse.getGenderPolicyType().toString())
                .contractTerm(selectedHouse.getContractTerm())
                .moodTags(moodTags)
                .roomMood(selectedHouse.getRoomMood())
                .groundRule(groundRules)
                .isPinned(isPinned)
                .safetyLivingFacility(safetyLivingFacilities)
                .kitchenFacility(kitchenFacilities)
                .build();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        List<RoomDto> roomDtos = rooms.stream()
                .sorted(Comparator.comparing(Room::getId))
                .map(room -> RoomDto.builder()
                        .roomId(room.getId())
                        .name(room.getName())
                        .status(room.getStatus() != room.getOccupancyType())
                        .occupancyType(room.getOccupancyType())
                        .gender(room.getGenderType().toString())
                        .deposit(room.getDeposit())
                        .prepaidUtilities(room.getPrepaidUtilities())
                        .monthlyRent(room.getMonthlyRent())
                        .contractPeriod(dateFormat.format(room.getContractPeriod()))
                        .managementFee(room.getManagementFee())
                        .build())
                .collect(Collectors.toList());

        List<RoommateDto> roommateDtos = rooms.stream()
                .flatMap(room -> room.getRoommates().stream()
                        .map(roommate -> RoommateDto.builder()
                                .name(room.getName())
                                .age(roommate.getAge())
                                .job(roommate.getJob())
                                .mbti(roommate.getMbti())
                                .sleepTime(roommate.getSleepTime())
                                .activityTime(roommate.getActivateTime())
                                .build()))
                .collect(Collectors.toList());
        return HouseDetailsResponseDto.builder()
                .houseInfo(houseInfoDto)
                .rooms(roomDtos)
                .roommates(roommateDtos)
                .build();
    }
}
