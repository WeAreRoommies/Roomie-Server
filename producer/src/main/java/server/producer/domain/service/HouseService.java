package server.producer.domain.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import server.producer.domain.dto.response.*;
import server.producer.domain.repository.PinRepository;
import server.producer.domain.dto.response.PinnedListResponseDto;
import entity.House;
import entity.Pin;
import entity.Room;

import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
		if (userId == null || userId <= 0) {
			throw new InvalidParameterException("Invalid userId: " + userId);
		}
		List<House> pinnedHouses = houseRepository.findPinnedHouseByUserId(userId);
		List<PinnedListResponseDto.PinnedHouseDto> pinnedHouseDtos = pinnedHouses.stream()
				.map(house -> PinnedListResponseDto.PinnedHouseDto.builder()
						.houseId(house.getId())
						.monthlyRent(house.calculateMonthlyRent())
						.deposit(house.calculateDeposit())
						.occupancyTypes(house.calculateOccupancyType())
						.location(house.getLocation())
						.genderPolicy(house.getGenderPolicy().toString())
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
		String location = userRepository.findLocationById(userId)
				.orElseThrow(()-> new EntityNotFoundException("User location not found."));
		List<House> houses = houseRepository.findByLocationAndMoodTag(location, moodTag);
		//결과가 없을 경우 빈 리스트 반환
		if (houses.isEmpty()) {
			return MoodHouseResponseDto.builder()
					.moodTag(moodTag)
					.houses(Collections.emptyList())
					.build();
		}
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
					.genderPolicy(house.getGenderPolicy().toString())
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
		House selectedHouse = houseRepository.findHouseWithRoomsById(houseId)
				.orElseThrow(() -> new EntityNotFoundException("해당 House를 찾을 수 없습니다."));

		List<Room> rooms = houseRepository.findRoomsAndRoommatesByHouseId(houseId);

		if (rooms.isEmpty()) {
			throw new EntityNotFoundException("해당 매물에 방 정보가 없습니다.");
		}

		boolean isPinned = houseRepository.findHouseWithPinsById(houseId)
				.map(h -> h.getPins().stream()
						.anyMatch(pin -> pin.getUser().getId().equals(userId)))
				.orElse(false);

        final List<String> groundRules = Arrays.stream(selectedHouse.getGroundRule().split("#")).toList();
        final List<String> safetyLivingFacilities = Arrays.stream(selectedHouse.getSafetyLivingFacility().split("#")).toList();
        final List<String> kitchenFacilities = Arrays.stream(selectedHouse.getKitchenFacility().split("#")).toList();

        HouseDetailsResponseDto.HouseInfoDto houseInfoDto = HouseDetailsResponseDto.HouseInfoDto.builder()
                .houseId(houseId)
                .name(selectedHouse.getName())
                .mainImgUrl(selectedHouse.getMainImgUrl())
                .monthlyRent(selectedHouse.calculateMonthlyRent())
                .deposit(selectedHouse.calculateDeposit())
                .location(selectedHouse.getLocation())
                .occupancyTypes(selectedHouse.calculateOccupancyType())
                .occupancyStatus(selectedHouse.calculateOccupancyStatus())
                .genderPolicy(selectedHouse.getGenderPolicy().toString())
                .contractTerm(selectedHouse.getContractTerm())
                .moodTags(selectedHouse.mergeTags())
                .roomMood(selectedHouse.getRoomMood())
                .groundRule(groundRules)
                .isPinned(isPinned)
                .safetyLivingFacility(safetyLivingFacilities)
                .kitchenFacility(kitchenFacilities)
                .build();

        List<HouseDetailsResponseDto.RoomDto> roomDtos = rooms.stream()
                .sorted(Comparator.comparing(Room::getId))
                .map(room -> HouseDetailsResponseDto.RoomDto.builder()
                        .roomId(room.getId())
                        .name(room.getName())
                        .status(room.getStatus() != room.getOccupancyType())
                        .occupancyType(room.getOccupancyType())
                        .gender(room.getGender().toString())
                        .deposit(room.getDeposit())
                        .prepaidUtilities(room.getPrepaidUtilities())
                        .monthlyRent(room.getMonthlyRent())
                        .contractPeriod(room.getContractPeriod())
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
                                .activityTime(roommate.getActivityTime())
                                .build()))
                .collect(Collectors.toList());
        return HouseDetailsResponseDto.builder()
                .houseInfo(houseInfoDto)
                .rooms(roomDtos)
                .roommates(roommateDtos)
                .build();
    }

	public boolean togglePin(Long userId, Long houseId) {
		try {
			// 핀 존재 여부 확인
			Optional<Pin> existingPin = pinRepository.findByUserIdAndHouseId(userId, houseId);
			if (existingPin.isPresent()) {
				// 핀 삭제
				pinRepository.deleteByUserIdAndHouseId(userId, houseId);
				return false;
			} else {
				// 핀 생성
				Pin pin = new Pin();
				pin.setUser(userRepository.getReferenceById(userId)); // 유저 참조
				pin.setHouse(houseRepository.getReferenceById(houseId)); // 매물 참조
				pinRepository.save(pin);
				return true;
			}
		} catch (EntityNotFoundException e) {
			// 유저나 매물이 없는 경우
			throw new EntityNotFoundException("User or House not found: " + e.getMessage());
		} catch (DataAccessException e) {
			// 데이터베이스 관련 예외
			throw new RuntimeException("Database error occurred while toggling pin.", e);
		} catch (Exception e) {
			// 기타 예외
			throw new RuntimeException("An unexpected error occurred.", e);
		}
	}

	public ImageDetailsResponseDto getHouseImages(Long houseId) {
		if (houseId == null || houseId <= 0) {
			throw new InvalidParameterException("Invalid houseId: " + houseId);
		}
		House house = houseRepository.findById(houseId)
				.orElseThrow(()-> new EntityNotFoundException("House not found."));
		return ImageDetailsResponseDto.builder()
				.images(ImageDetailsResponseDto.Images.builder()
						.mainImgUrl(house.getMainImgUrl())
						.mainImgDescription(house.getMainImgDescription())
						.facilityImgUrls(Arrays.asList(house.getFacilityImgUrl().split(" ")))
						.facilityImgDescription(house.getFacilityImgDescription())
						.floorImgUrl(house.getFloorImgUrl())
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
