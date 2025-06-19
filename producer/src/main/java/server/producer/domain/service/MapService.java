package server.producer.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import server.producer.common.LocationLabeler;
import server.producer.domain.dto.request.FilterRequestDto;
import server.producer.domain.dto.response.FilterResponseDto;
import server.producer.domain.repository.FilterRepository;
import server.producer.domain.repository.UserRepository;
import entity.House;
import entity.User;
import jakarta.persistence.EntityNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


@Service
@RequiredArgsConstructor
public class MapService {
	private final FilterRepository filterRepository;
	private final UserRepository userRepository;
	public FilterResponseDto searchProperties(FilterRequestDto requestDto, Long userId){
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new EntityNotFoundException("User not found"));

		double latitude = user.getLatitude();
		double longitude = user.getLongitude();

		// 분위기 태그 필터링 분기
		List<String> moodTags = requestDto.getMoodTags();
		if (moodTags == null || moodTags.isEmpty()) {
			// 분위기 태그가 없으면 조건 무시
			requestDto.setMoodTags(null);
		} else {
			// 분위기 태그가 있으면 OR 조건으로 검색
			requestDto.setMoodTags(moodTags);
		}

		List<FilterResponseDto.HouseMapDto> houseMapDtos = new ArrayList<>();
		List<House> houses = filterRepository.findFilteredHouses(requestDto);
		for (House house : houses) {
			final boolean isPinned = house.getPins().stream()
					.anyMatch(pin -> pin.getUser().getId().equals(userId));
			final boolean isFull = house.isFull();
			FilterResponseDto.HouseMapDto dto = FilterResponseDto.HouseMapDto.builder()
					.houseId(house.getId())
					.latitude(house.getLatitude())
					.longitude(house.getLongitude())
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
					.excludeFull(isFull)
					.build();
			houseMapDtos.add(dto);
		}
		return FilterResponseDto.builder()
			.houses(houseMapDtos)
			.latitude(latitude)
			.longitude(longitude)
			.build();
	}
}
