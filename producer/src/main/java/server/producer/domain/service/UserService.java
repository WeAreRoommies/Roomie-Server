package server.producer.domain.service;

import entity.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import server.producer.domain.dto.request.LocationUpdateRequestDto;
import server.producer.domain.dto.response.AccountInfoResponseDto;
import server.producer.domain.dto.response.HomeInfoResponseDto;
import server.producer.domain.dto.response.LocationUpdateResponseDto;
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
		final String location = user.getLocation().split(" ")[2];
		final List<HomeInfoResponseDto.RecentlyViewedHouseDto> recentlyViewedHouseDtos = new ArrayList<>();

		List<RecentlyViewedHouse> houses = recentlyViewedHouseRepository.findByUserIdOrderByViewedAtDesc(user.getId());

		for (RecentlyViewedHouse rvh : houses) {
			House house = rvh.getHouse();
			final boolean isPinned = house.getPins().stream()
					.anyMatch(pin -> pin.getUser().getId().equals(userId));
			HomeInfoResponseDto.RecentlyViewedHouseDto dto = new HomeInfoResponseDto.RecentlyViewedHouseDto(
					house.getId(),
					house.calculateMonthlyRent(),
					house.calculateDeposit(),
					house.calculateOccupancyType(),
					house.getLocation(),
					house.getGenderPolicy().toString(),
					house.getLocationDescription(),
					isPinned,
					house.getMoodTag(),
					house.getContractTerm(),
					house.getMainImgUrl()
			);
			recentlyViewedHouseDtos.add(dto);
		}
		return new HomeInfoResponseDto(nickname, location, recentlyViewedHouseDtos);
	}

	public MyPageResponseDto getMyPage(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException("User not found"));
		final String nickname = user.getNickname();
		final SocialType socialType = user.getSocialType();
		return new MyPageResponseDto(nickname, socialType);
	}

	@Transactional
	public void updateNickname(Long userId, String newNickname) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("사용자 없음"));
		user.setName(newNickname);
	}

	@Transactional
	public void updateName(Long userId, String newName) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException("사용자 없음"));
		user.setName(newName);
	}

	@Transactional
	public void updateBirthDay(Long userId, LocalDate birthDay) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException("사용자 없음"));
		user.setBirthDate(birthDay);
	}

	@Transactional
	public void updatePhoneNumber(Long userId, String phoneNumber) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException("사용자 없음"));
		user.setPhoneNumber(phoneNumber);
	}

	@Transactional
	public void updateGender(Long userId, Gender gender) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException("사용자 없음"));
		user.setGender(gender);
	}

	public AccountInfoResponseDto getAccountInfo(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException("User not found"));

		return new AccountInfoResponseDto(
				user.getNickname(),
				user.getSocialType(),
				user.getName(),
				user.getBirthDate(),
				user.getPhoneNumber(),
				user.getGender()
		);
	}

    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        userRepository.delete(user);
	}

    public LocationUpdateResponseDto updateLocation(Long userId, LocationUpdateRequestDto requestDto) {
        try {
            String[] locationParts = requestDto.getLocation().split(" ");
            if (locationParts.length >= 3) {
                if (locationParts[0].equals("서울특별시")) {
                    User user = userRepository.findById(userId)
                        .orElseThrow(() -> new EntityNotFoundException("User not found"));
                    user.setLatitude(requestDto.getLatitude());
                    user.setLongitude(requestDto.getLongitude());
                    String location = locationParts[0] + " " + locationParts[1] + " " + locationParts[2];
                    user.setLocation(location);
                    userRepository.save(user);
                    return new LocationUpdateResponseDto(
                        user.getLatitude(),
                        user.getLongitude(),
                        user.getLocation()
                    );
                } else {
                    throw new IllegalArgumentException("Invalid location format");
                }
            }
            throw new IllegalArgumentException("Invalid location format");
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid location format");
        }
    }
	
}
