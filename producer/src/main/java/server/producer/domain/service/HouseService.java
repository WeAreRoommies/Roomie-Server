package server.producer.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import server.producer.domain.dto.response.MoodHouseResponseDto;
import server.producer.domain.repository.HouseRepository;
import server.producer.domain.repository.UserRepository;
import server.producer.entity.House;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HouseService {
	private final HouseRepository houseRepository;
	private final UserRepository userRepository;

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
}
