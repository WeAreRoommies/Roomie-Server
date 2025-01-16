package server.producer.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import server.producer.domain.dto.response.HomeInfoResponseDto;
import server.producer.domain.dto.response.MyPageResponseDto;
import server.producer.domain.repository.UserRepository;
import entity.House;
import entity.RecentlyViewedHouse;
import entity.Room;
import entity.User;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	public HomeInfoResponseDto getUserInfoAndRecentlyViewedHouse(Long userId) {
		// 사용자 정보 조회
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));
		final String name = user.getName();
		final String location = user.getLocation().split(" ")[user.getLocation().split(" ").length-1];
		final List<HomeInfoResponseDto.RecentlyViewedHouseDto> recentlyViewedHouseDtos = new ArrayList<>();

		List<House> houses = user.getRecentlyViewedHouses().stream()
				.map(RecentlyViewedHouse::getHouse)
				.toList();

		for (House house : houses) {
			List<Room> rooms = house.getRooms();
			final boolean isPinned = house.getPins().stream()
					.anyMatch(pin -> pin.getUser().getId().equals(userId));
			HomeInfoResponseDto.RecentlyViewedHouseDto dto = HomeInfoResponseDto.RecentlyViewedHouseDto.builder()
					.houseId(house.getId())
					.monthlyRent(house.calculateMonthlyRent())
					.deposit(house.calculateDeposit())
					.occupancyTypes(house.calculateOccupancyType())
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

	public MyPageResponseDto getMyPage(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));
		final String name = user.getName();
		return MyPageResponseDto.builder()
				.name(name)
				.build();
	}
}
