package server.producer.domain.service;

import entity.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import server.producer.common.LocationLabeler;
import server.producer.domain.dto.response.*;
import server.producer.domain.repository.PinRepository;
import server.producer.domain.dto.response.PinnedListResponseDto;
import entity.House;
import entity.Pin;
import entity.Room;

import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import server.producer.domain.repository.HouseRepository;
import server.producer.domain.repository.RecentlyViewedHouseRepository;
import server.producer.domain.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class HouseService {
	private final HouseRepository houseRepository;
	private final UserRepository userRepository;
	private final PinRepository pinRepository;
	private final RecentlyViewedHouseRepository recentlyViewedHouseRepository;
	private final LocationLabeler locationLabeler;

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
		String gu = location.split(" ")[0];
		int label = locationLabeler.findLabelByLocation(gu);
		List<House> houses = houseRepository.findByLabelAndMoodTag(label, moodTag);
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

	@Transactional
	public void upsertRecentlyViewedHouse(Long houseId, Long userId) {
		// 데이터 존재 여부 확인
		RecentlyViewedHouse existingEntry = recentlyViewedHouseRepository.findByUserIdAndHouseId(userId, houseId)
				.orElse(new RecentlyViewedHouse()); // 없으면 새 엔티티 생성

		// 데이터 설정
		User user = userRepository.getReferenceById(userId);
		House house = houseRepository.getReferenceById(houseId);
		existingEntry.setUser(user);
		existingEntry.setHouse(house);
		existingEntry.setViewedAt(LocalDateTime.now()); // 현재 시간 업데이트

		// 저장 (존재하면 업데이트, 없으면 삽입)
		recentlyViewedHouseRepository.save(existingEntry);
	}

    public HouseDetailsResponseDto getHouseDetails(final Long houseId, final Long userId) {
		House selectedHouse = houseRepository.findHouseWithRoomsById(houseId)
				.orElseThrow(() -> new EntityNotFoundException("해당 House를 찾을 수 없습니다."));

		List<Room> rooms = houseRepository.findRoomsAndRoomOccupanciesByHouseId(houseId);

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
                        .status(room.getCurrentOccupancyCount())
                        .isTourAvailable(room.isTourAvailable())
                        .occupancyType(room.getOccupancyType())
                        .gender(room.getGender().toString())
                        .deposit(room.getDeposit())
                        .monthlyRent(room.getMonthlyRent())
                        .contractPeriod(room.getContractPeriod())
                        .managementFee(room.getManagementFee())
                        .roomOccupancies(room.getRoomOccupancies().stream()
                                .map(occupancy -> HouseDetailsResponseDto.RoomOccupancyDto.builder()
                                        .name(occupancy.getName())
                                        .isOccupied(occupancy.isOccupied())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());

		upsertRecentlyViewedHouse(houseId, userId);

        return HouseDetailsResponseDto.builder()
                .houseInfo(houseInfoDto)
                .rooms(roomDtos)
                .build();
    }

	public PinnedResponseDto togglePin(Long userId, Long houseId) {
		try {
			List<Pin> existingPins = pinRepository.findByUserIdAndHouseId(userId, houseId);
			if (!existingPins.isEmpty()) {
				pinRepository.deleteAllInBatch(existingPins); // 모든 엔티티 삭제
				return PinnedResponseDto.builder()
						.isPinned(false)
						.build();
			} else {
				Pin pin = new Pin();
				pin.setUser(userRepository.getReferenceById(userId));
				pin.setHouse(houseRepository.getReferenceById(houseId));
				pinRepository.save(pin);
				return PinnedResponseDto.builder()
						.isPinned(true)
						.build();
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
		if (houseId == null || houseId <= 0) {
			throw new InvalidParameterException("Invalid houseId: " + houseId);
		}
		List<Room> rooms = houseRepository.findAllRoomsByHouseId(houseId);
		if (rooms.isEmpty()) {
			throw new EntityNotFoundException("No rooms found for houseId: " + houseId);
		}
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
