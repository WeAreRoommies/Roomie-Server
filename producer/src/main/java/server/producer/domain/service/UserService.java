package server.producer.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import server.producer.domain.RoomStatistics;
import server.producer.domain.dto.response.HomeInfoResponseDto;
import server.producer.domain.repository.HouseRepository;
import server.producer.domain.repository.RoomRepository;
import server.producer.domain.repository.UserRepository;
import server.producer.entity.House;
import server.producer.entity.RecentlyViewedHouse;
import server.producer.entity.Room;
import server.producer.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final HouseRepository houseRepository;

	public HomeInfoResponseDto getUserInfoAndRecentlyViewedHouse(Long userId) {
		// 사용자 정보 조회
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));
		final String name = user.getName();
		final String location = user.getLocation().split(" ")[user.getLocation().split(" ").length-1];
		final List<HomeInfoResponseDto.RecentlyViewedHouseDto> recentlyViewedHouseDtos = new ArrayList<>();

		List<House> Houses = user.getRecentlyViewedHouses().stream()
				.map(RecentlyViewedHouse::getHouse)
				.toList();

		for (House house : Houses) {
			List<Room> rooms = house.getRooms();
			final String montlyRent = RoomStatistics.calculateMonthlyRent(rooms);
			final String deposit = RoomStatistics.calculateDeposit(rooms);
			final String occupancyType = RoomStatistics.calculateOccupancyType(rooms);
			final boolean isPinned = house.getPins().stream()
					.anyMatch(pin -> pin.getUser().getId().equals(userId));
			HomeInfoResponseDto.RecentlyViewedHouseDto dto = HomeInfoResponseDto.RecentlyViewedHouseDto.builder()
					.houseId(house.getId())
					.monthlyRent(montlyRent)
					.deposit(deposit)
					.occupancyTypes(occupancyType)
					.genderPolicy(house.getGenderPolicyType().toString())
					.locationDescription(house.getLocationDescription())
					.isPinned(isPinned)
					.moodTag(house.getMoodTag())
					.contractTerm(house.getContractTerm())
					.mainImgUrl(house.getMainImgUrl())
					.build();
			recentlyViewedHouseDtos.add(dto);
		}
		return HomeInfoResponseDto.builder()
				.name(name)
				.location(location)
				.recentlyViewedHouses(recentlyViewedHouseDtos)
				.build();
	}
}