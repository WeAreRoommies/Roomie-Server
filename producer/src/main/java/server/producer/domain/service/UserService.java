package server.producer.domain.service;

import entity.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import server.producer.domain.dto.response.HomeInfoResponseDto;
import server.producer.domain.dto.response.MyPageResponseDto;
import server.producer.domain.repository.HouseRepository;
import server.producer.domain.repository.RecentlyViewedHouseRepository;
import server.producer.domain.repository.UserRepository;

import java.security.InvalidParameterException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
		final String nickname = user.getNickname();
		final String location = user.getLocation().split(" ")[user.getLocation().split(" ").length-1];
		final List<HomeInfoResponseDto.RecentlyViewedHouseDto> recentlyViewedHouseDtos = new ArrayList<>();

		List<RecentlyViewedHouse> houses = recentlyViewedHouseRepository.findByUserIdOrderByViewedAtDesc(user.getId());

		for (RecentlyViewedHouse rvh : houses) {
			House house = rvh.getHouse();
			final boolean isPinned = house.getPins().stream()
					.anyMatch(pin -> pin.getUser().getId().equals(userId));
			HomeInfoResponseDto.RecentlyViewedHouseDto dto = HomeInfoResponseDto.RecentlyViewedHouseDto.builder()
					.houseId(house.getId())
					.monthlyRent(house.calculateMonthlyRent()) 
					.deposit(house.calculateDeposit())
					.occupancyTypes(house.calculateOccupancyType())
					.location(house.getLocation())
					.genderPolicy(house.getGenderPolicy().toString())
					.locationDescription(house.getLocationDescription())
					.isPinned(isPinned)
					.moodTag(house.getMoodTag())
					.contractTerm(house.getContractTerm())
					.mainImgUrl(house.getMainImgUrl())
					.build();
			recentlyViewedHouseDtos.add(dto);
		}
		return HomeInfoResponseDto.builder()
				.nickname(nickname)
				.location(location)
				.recentlyViewedHouses(recentlyViewedHouseDtos)
				.build();
	}

	public MyPageResponseDto getMyPage(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException("User not found"));
		final String nickname = user.getNickname();
		return MyPageResponseDto.builder()
				.nickname(nickname)
				.build();
	}

	public void updateNickname(Long userId, String newNickname) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("사용자 없음"));
		user.setName(newNickname);
	}

	public void updateName(Long userId, String newName) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException("사용자 없음"));
		user.setName(newName);
	}

	public void updateBirthDay(Long userId, LocalDate birthDay) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException("사용자 없음"));
		user.setBirthDate(birthDay);
	}

	public void updatePhoneNumber(Long userId, String phoneNumber) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException("사용자 없음"));
		user.setPhoneNumber(phoneNumber);
	}

	public void updateGender(Long userId, Gender gender) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException("사용자 없음"));
		user.setGender(gender);
	}
}
