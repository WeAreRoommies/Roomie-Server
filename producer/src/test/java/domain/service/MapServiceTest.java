package domain.service;

import entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import server.producer.domain.dto.request.FilterRequestDto;
import server.producer.domain.dto.response.FilterResponseDto;
import server.producer.domain.repository.FilterRepository;
import server.producer.domain.service.MapService;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class MapServiceTest {
	@Mock
	private FilterRepository filterRepository;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@InjectMocks
	private MapService mapService;

	@Test
	void testsearchProperties(){
		// HouseRepository 동작 설정
		House house1 = createHouse(1L, "#차분한", "서대문구 대현동", "자이아파트", true, 6, "https://example.com/images/house1.jpg");
		House house2 = createHouse(2L, "#차분한", "서대문구 대현동", "자이아파트", true, 6, "https://example.com/images/house2.jpg");

		// given
		List<String> genderPolicy = new ArrayList<String>();
		genderPolicy.add("여성전용");
		genderPolicy.add("남성전용");
		List<String> occp = List.of("1인실", "2인실");
		List<Integer> contractPeiods = List.of(6,12);
		Long userId = 1L;
		FilterRequestDto requestDto = new FilterRequestDto(
				"서울특별시 서대문구 대현동 11-1",
				"#차분한",
				new FilterRequestDto.Range(0, 10000000),
				new FilterRequestDto.Range(0,10000000),
				genderPolicy,
				null,
				occp,
				contractPeiods
				);

		// Mock
		when(filterRepository.findFilteredHouses(any())).thenReturn(List.of(house1, house2));

		// when
		FilterResponseDto result = mapService.searchProperties(requestDto, userId);

		//then
		FilterResponseDto.HouseMapDto result1 = result.houses().get(0);
		assertEquals(result.houses().size(), 2);
		assertEquals(result1.location(), "서대문구 대현동");
		assertEquals(result1.genderPolicy(), "여성전용");
		assertEquals(result1.moodTag(), "#차분한");
		assertEquals(result1.contractTerm(), 6);
	}

	private House createHouse(Long id, String moodTag,
							  String location, String locationDescription,
							  boolean isPinned, int contractTerm, String mainImgUrl) {
		House house = new House();
		house.setId(id);
		house.setName("Sample House " + id);
		house.setLatitude(37.5665 + id * 0.001); // 예제 좌표
		house.setLongitude(126.978 + id * 0.001); // 예제 좌표
		house.setRoomMood("Cozy");
		house.setGroundRule("No smoking");
		house.setLocation(location);
		house.setLocationDescription(locationDescription);
		house.setMoodTag(moodTag);
		house.setSubMoodTag("#추가태그");
		house.setGenderPolicy(GenderPolicyType.여성전용); // Enum 변환
		house.setMainImgUrl(mainImgUrl);
		house.setMainImgDescription("Main image of house " + id);
		house.setFacilityImgUrl("https://example.com/images/facility" + id + ".jpg");
		house.setFacilityImgDescription("Facility image of house " + id);
		house.setContractTerm(contractTerm);
		house.setSafetyLivingFacility("소화기");
		house.setKitchenFacility("냉장고");

		// Room 생성 및 추가
		List<Room> rooms = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			Room room = new Room();
			room.setId((id * 10) + i); // 유니크한 Room ID
			room.setHouse(house);
			room.setName("Room " + (i + 1));
			room.setMonthlyRent(300000 + (i * 50000));
			room.setDeposit(1000000 + (i * 1000000));
			room.setOccupancyType(i + 1);
			room.setStatus(0); // 활성 상태
			room.setMainImgUrl("https://example.com/images/room" + ((id * 10) + i) + ".jpg");
			room.setPrepaidUtilities(50000);
			room.setManagementFee("100000");
			rooms.add(room);
		}
		house.setRooms(rooms);
		// Pin 생성 및 추가
		if (isPinned) {
			Pin pin = new Pin();
			pin.setId(id);
			pin.setHouse(house);
			User user = new User();
			user.setId(1L); // Mock user
			user.setName("Test User");
			pin.setUser(user);
			house.setPins(List.of(pin));
		}
		return house;
	}
}
