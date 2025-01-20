package server.producer.domain.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import server.producer.domain.dto.response.HomeInfoResponseDto;
import server.producer.domain.dto.response.MyPageResponseDto;
import server.producer.domain.repository.HouseRepository;
import server.producer.domain.repository.RecentlyViewedHouseRepository;
import server.producer.domain.repository.UserRepository;
import entity.House;
import entity.RecentlyViewedHouse;
import entity.Room;
import entity.User;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final HouseRepository houseRepository;
	private final RecentlyViewedHouseRepository recentlyViewedHouseRepository;

	public HomeInfoResponseDto getUserInfoAndRecentlyViewedHouse(Long userId) {
		if (userId == null || userId <= 0) {
			throw new InvalidParameterException("Invalid userId: " + userId);
		}
		// 사용자 정보 조회
		User user = userRepository.findUserWithRecentlyViewedHouses(userId)
				.orElseThrow(EntityNotFoundException::new);
		final String name = user.getName();
		final String location = user.getLocation().split(" ")[user.getLocation().split(" ").length-1];
		final List<HomeInfoResponseDto.RecentlyViewedHouseDto> recentlyViewedHouseDtos = new ArrayList<>();

		List<RecentlyViewedHouse> houses = recentlyViewedHouseRepository.findByUserIdOrderByViewedAtDesc(user.getId());

		for (RecentlyViewedHouse rvh : houses) {
			House house = rvh.getHouse();
			List<Room> rooms = house.getRooms();
			final boolean isPinned = house.getPins().stream()
					.anyMatch(pin -> pin.getUser().getId().equals(userId));
			HomeInfoResponseDto.RecentlyViewedHouseDto dto = HomeInfoResponseDto.RecentlyViewedHouseDto.builder()
					.houseId(house.getId())
					.monthlyRent(house.calculateMonthlyRent())
					.deposit(house.calculateDeposit())
					.occupancyTypes(house.calculateOccupancyType())
					.genderPolicy(house.getGenderPolicy().toString())
					.location(house.getLocation())
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
				.orElseThrow(() -> new EntityNotFoundException("User not found"));
		final String name = user.getName();
		return MyPageResponseDto.builder()
				.name(name)
				.build();
	}
}
