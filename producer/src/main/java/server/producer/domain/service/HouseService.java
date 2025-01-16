package server.producer.domain.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import server.producer.domain.dto.response.*;
import server.producer.domain.repository.PinRepository;
import server.producer.domain.dto.response.PinnedListResponseDto;
import entity.House;
import entity.Pin;
import entity.Room;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import server.producer.domain.repository.HouseRepository;
import server.producer.domain.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class HouseService {
	private final HouseRepository houseRepository;
	private final UserRepository userRepository;
	private final PinRepository pinRepository;

	public PinnedListResponseDto getPinnedHouses(Long userId) {
		List<House> pinnedHouses = houseRepository.findPinnedHouseByUserId(userId);
		List<PinnedListResponseDto.PinnedHouseDto> pinnedHouseDtos = pinnedHouses.stream()
				.map(house -> PinnedListResponseDto.PinnedHouseDto.builder()
						.houseId(house.getId())
						.monthlyRent(house.calculateMonthlyRent())
						.deposit(house.calculateDeposit())
						.occupancyTypes(house.calculateOccupancyType())
						.location(house.getLocation())
						.genderPolicy(house.getGenderPolicyType().toString())
						.locationDescription(house.getLocationDescription())
						.isPinned(true)
						.contractTerm(house.getContractTerm())
						.mainImgUrl(house.getMainImgUrl())
						.moodTag(house.getMoodTag())
						.build())
				.collect(Collectors.toList());
		return PinnedListResponseDto.builder()
				.pinnedHouses(pinnedHouseDtos)
				.build();
	}

	public MoodHouseResponseDto getHousesByMoodAndLocation(String moodTag, Long userId){
		String location = userRepository.findLocationById(userId).orElseThrow(RuntimeException::new);
		List<House> houses = houseRepository.findByLocationAndMoodTag(location, moodTag);
		List<MoodHouseResponseDto.MoodHouseDto> moodHouseDtos = new ArrayList<>();
		for (House house : houses) {
			final boolean isPinned = house.getPins().stream()
					.anyMatch(pin -> pin.getUser().getId().equals(userId));
			MoodHouseResponseDto.MoodHouseDto dto = MoodHouseResponseDto.MoodHouseDto.builder()
					.houseId(house.getId())
					.monthlyRent(house.calculateMonthlyRent())
					.deposit(house.calculateDeposit())
					.occupancyTypes(house.calculateOccupancyType())
					.location(location)
					.genderPolicy(house.getGenderPolicyType().toString())
					.locationDescription(house.getLocationDescription())
					.isPinned(isPinned)
					.contractTerm(house.getContractTerm())
					.mainImgUrl(house.getMainImgUrl())
					.build();
			moodHouseDtos.add(dto);
		}
		return MoodHouseResponseDto.builder()
				.moodTag(moodTag)
				.houses(moodHouseDtos).build();
	}

    public HouseDetailsResponseDto getHouseDetails(final Long houseId, final Long userId) {
        House selectedHouse = houseRepository.findHouseDetailsById(houseId)
                .orElseThrow(()->new IllegalArgumentException("해당 House를 찾을 수 없습니다."));
        List<Room> rooms = selectedHouse.getRooms();
        final List<String> groundRules = Arrays.stream(selectedHouse.getGroundRule().split("#")).toList();
        final List<String> safetyLivingFacilities = Arrays.stream(selectedHouse.getSafetyLivingFacility().split("#")).toList();
        final List<String> kitchenFacilities = Arrays.stream(selectedHouse.getKitchenFacility().split("#")).toList();

        final boolean isPinned = selectedHouse.getPins().stream()
                .anyMatch(pin -> pin.getUser().getId().equals(userId));
        HouseDetailsResponseDto.HouseInfoDto houseInfoDto = HouseDetailsResponseDto.HouseInfoDto.builder()
                .houseId(houseId)
                .name(selectedHouse.getName())
                .mainImgUrl(selectedHouse.getMainImgUrl())
                .monthlyRent(selectedHouse.calculateMonthlyRent())
                .deposit(selectedHouse.calculateDeposit())
                .location(selectedHouse.getLocation())
                .occupancyTypes(selectedHouse.calculateOccupancyType())
                .occupancyStatus(selectedHouse.calculateOccupancyStatus())
                .genderPolicy(selectedHouse.getGenderPolicyType().toString())
                .contractTerm(selectedHouse.getContractTerm())
                .moodTags(selectedHouse.mergeTags())
                .roomMood(selectedHouse.getRoomMood())
                .groundRule(groundRules)
                .isPinned(isPinned)
                .safetyLivingFacility(safetyLivingFacilities)
                .kitchenFacility(kitchenFacilities)
                .build();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        List<HouseDetailsResponseDto.RoomDto> roomDtos = rooms.stream()
                .sorted(Comparator.comparing(Room::getId))
                .map(room -> HouseDetailsResponseDto.RoomDto.builder()
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

        List<HouseDetailsResponseDto.RoommateDto> roommateDtos = rooms.stream()
                .flatMap(room -> room.getRoommates().stream()
                        .map(roommate -> HouseDetailsResponseDto.RoommateDto.builder()
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

	public boolean togglePin(Long userId, Long houseId) {
		Optional<Pin> existingPin = pinRepository.findByUserIdAndHouseId(userId, houseId);
		if (existingPin.isPresent()) {
			pinRepository.deleteByUserIdAndHouseId(userId, houseId);
			return false;
		} else {
			Pin pin = new Pin();
			pin.setUser(userRepository.getReferenceById(userId));
			pin.setHouse(houseRepository.getReferenceById(houseId));
			pinRepository.save(pin);
			return true;
		}
	}

	public ImageDetailsResponseDto getHouseImages(Long houseId) {
		House house = houseRepository.findById(houseId)
				.orElseThrow(()-> new EntityNotFoundException("House not found."));
		return ImageDetailsResponseDto.builder()
				.images(ImageDetailsResponseDto.Images.builder()
						.mainImgUrl(house.getMainImgUrl())
						.mainImgDescription(house.getMainImgDescription())
						.facilityImgUrls(Arrays.asList(house.getFacilityImgUrl().split(" ")))
						.facilityImgDescription(house.getFacilityImgDescription())
						.floorImgUrl(house.getLocationDescription())
						.build())
				.build();
	}

	public RoomDetailsResponseDto getHouseRooms(Long houseId) {
		List<Room> rooms = houseRepository.findAllRoomsByHouseId(houseId);
		List<RoomDetailsResponseDto.Room> roomDtos = rooms.stream()
				.sorted(Comparator.comparing(Room::getId))
				.map(room -> RoomDetailsResponseDto.Room.builder()
						.roomId(room.getId())
						.name(room.getName())
						.facility(Arrays.asList(room.getFacility().split("#")))
						.status(room.getStatus() != room.getOccupancyType())
						.mainImageUrl(Arrays.asList(room.getMainImgUrl().split(" ")))
						.build())
				.toList();
		return RoomDetailsResponseDto.builder()
				.rooms(roomDtos)
				.build();
	}
}
