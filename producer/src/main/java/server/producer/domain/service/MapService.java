package server.producer.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import server.producer.domain.dto.request.FilterRequestDto;
import server.producer.domain.dto.response.FilterResponseDto;
import server.producer.domain.repository.FilterRepository;
import entity.House;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MapService {
	private final FilterRepository filterRepository;

	public FilterResponseDto searchProperties(FilterRequestDto requestDto, Long userId){
		String location = requestDto.location();
		if (location == null || location.isBlank()) {
			throw new IllegalArgumentException();
		}
		String[] parts = location.split(" ");
		String gu = parts[1];
		String dong = parts[2];

		FilterRequestDto updated;
		if (gu != null && dong != null) {
			location =  gu + " " + dong;
			updated = new FilterRequestDto(
					location, // 변경된 location
					requestDto.moodTag(),
					requestDto.depositRange(),
					requestDto.monthlyRentRange(),
					requestDto.genderPolicy(),
					requestDto.preferredDate(),
					requestDto.occupancyTypes(),
					requestDto.contractPeriod()
			);

			List<FilterResponseDto.HouseMapDto> houseMapDtos = new ArrayList<>();
			List<House> houses = filterRepository.findFilteredHouses(updated);
			for (House house : houses) {
				final boolean isPinned = house.getPins().stream()
						.anyMatch(pin -> pin.getUser().getId().equals(userId));
				FilterResponseDto.HouseMapDto dto = FilterResponseDto.HouseMapDto.builder()
						.houseId(house.getId())
						.x(house.getLongitude())
						.y(house.getLatitude())
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
				houseMapDtos.add(dto);
			}
			return FilterResponseDto.builder().houses(houseMapDtos).build();
		} else {
			throw new IllegalArgumentException();
		}

	}
}
