package domain.service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import server.producer.domain.dto.response.MoodHouseResponseDto;
import server.producer.domain.repository.HouseRepository;
import server.producer.domain.repository.UserRepository;
import server.producer.domain.service.HouseService;
import server.producer.entity.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class HouseServiceTest {

	@Mock
	private HouseRepository houseRepository;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private HouseService houseService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testGetHousesByMoodAndLocation() {
		// Mock 데이터 준비
		Long userId = 1L;
		String location = "서대문구 연희동";
		String moodTag = "#차분한";

		// UserRepository 동작 설정
		when(userRepository.findLocationById(anyLong())).thenReturn(Optional.of(location));

		// HouseRepository 동작 설정
		House house1 = createHouse(1L, moodTag, location, "자이아파트", true, 6, "https://example.com/images/house1.jpg");
		House house2 = createHouse(2L, moodTag, location, "자이아파트", true, 6, "https://example.com/images/house2.jpg");

		when(houseRepository.findByLocationAndMoodTag(eq(location), eq(moodTag)))
				.thenReturn(List.of(house1, house2));

		// 테스트 실행
		MoodHouseResponseDto result = houseService.getHousesByMoodAndLocation(moodTag, userId);

		// 검증
		assertEquals(moodTag, result.moodTag());
		assertEquals(2, result.houses().size());

		MoodHouseResponseDto.MoodHouseDto houseDto1 = result.houses().get(0);
		assertEquals(1L, houseDto1.houseId());
		assertEquals("30~40", houseDto1.monthlyRent());
		assertEquals(location, houseDto1.location());
		assertEquals("100~300", houseDto1.deposit());
		assertEquals("1,2,3인실", houseDto1.occupancyTypes());
		assertEquals(location, houseDto1.location());
		assertEquals("남녀공용", houseDto1.genderPolicy());
		assertEquals("자이아파트", houseDto1.locationDescription());
		assertEquals(true, houseDto1.isPinned());
		assertEquals(6, houseDto1.contractTerm());
		assertEquals("https://example.com/images/house1.jpg", houseDto1.mainImgUrl());

		MoodHouseResponseDto.MoodHouseDto houseDto2 = result.houses().get(1);
		assertEquals(2L, houseDto2.houseId());
		assertEquals("30~40", houseDto2.monthlyRent());
		assertEquals("100~300", houseDto2.deposit());
		assertEquals(moodTag, result.moodTag());

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
		house.setGenderPolicyType(GenderPolicyType.남녀공용); // Enum 변환
		house.setMainImgUrl(mainImgUrl);
		house.setMainImgDescription("Main image of house " + id);
		house.setFacilityImgUrl("https://example.com/images/facility" + id + ".jpg");
		house.setFacilityImgDescription("Facility image of house " + id);
		house.setContractTerm(contractTerm);
		house.setSafetyLivingFacilityType("소화기");
		house.setKitchenFacilityType("냉장고");

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
			room.setStatus(1); // 활성 상태
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
