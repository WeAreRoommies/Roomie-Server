package domain.service;
import entity.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import server.producer.domain.dto.response.*;
import server.producer.domain.repository.HouseRepository;
import server.producer.domain.repository.PinRepository;
import server.producer.domain.repository.UserRepository;
import server.producer.domain.service.HouseService;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class HouseServiceTest {

	@Mock
	private HouseRepository houseRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private PinRepository pinRepository;

	@InjectMocks
	private HouseService houseService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}
	@Test
	void testGetPinnedHouses() {
		// Given: Mock 데이터 준비
		Long userId = 1L;

		// House Mock 데이터 생성
		House house1 = createHouse(1L, "#활기찬", "서울 강남구", "자이 아파트", true, 12, "https://example.com/images/house1.jpg");
		House house2 = createHouse(2L, "#차분한", "서울 송파구", "오피스텔", true, 6, "https://example.com/images/house2.jpg");

		// houseRepository 동작 설정
		when(houseRepository.findPinnedHouseByUserId(userId)).thenReturn(List.of(house1, house2));

		// When: 서비스 호출
		PinnedListResponseDto response = houseService.getPinnedHouses(userId);

		// Then: 검증
		assertNotNull(response); // 응답이 null이 아님
		assertEquals(2, response.pinnedHouses().size()); // 반환된 집의 개수 검증

		// House 1 검증
		PinnedListResponseDto.PinnedHouseDto houseDto1 = response.pinnedHouses().get(0);
		assertEquals(1L, houseDto1.houseId());
		assertEquals("서울 강남구", houseDto1.location());
		assertEquals("자이 아파트", houseDto1.locationDescription());
		assertEquals("30~40", houseDto1.monthlyRent()); // Mock된 값에 따라 예상 값
		assertEquals("100~300", houseDto1.deposit());
		assertEquals("1,2,3인실", houseDto1.occupancyTypes());
		assertEquals("남녀공용", houseDto1.genderPolicy());
		assertEquals(true, houseDto1.isPinned());
		assertEquals(12, houseDto1.contractTerm());
		assertEquals("https://example.com/images/house1.jpg", houseDto1.mainImgUrl());
		assertEquals("#활기찬", houseDto1.moodTag());

		// House 2 검증
		PinnedListResponseDto.PinnedHouseDto houseDto2 = response.pinnedHouses().get(1);
		assertEquals(2L, houseDto2.houseId());
		assertEquals("서울 송파구", houseDto2.location());
		assertEquals("오피스텔", houseDto2.locationDescription());
		assertEquals("30~40", houseDto2.monthlyRent()); // Mock된 값에 따라 예상 값
		assertEquals("100~300", houseDto2.deposit());
		assertEquals("1,2,3인실", houseDto2.occupancyTypes());
		assertEquals("남녀공용", houseDto2.genderPolicy());
		assertEquals(true, houseDto2.isPinned());
		assertEquals(6, houseDto2.contractTerm());
		assertEquals("https://example.com/images/house2.jpg", houseDto2.mainImgUrl());
		assertEquals("#차분한", houseDto2.moodTag());
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

		// given
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

	@Test
	void testGetHouseDetails() {
		// Given: Mock 데이터 준비
		Long houseId = 1L;
		Long userId = 2L;

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy-MM-dd");

		// House, Room, Roommate, and Pin Mock 데이터 생성
		House mockHouse = createMockHouse(houseId, "루미 100호점(이대역)", "서울 강남구", "전반적으로 조용하고 깔끔한 환경을 선호하는 아침형 룸메이트들이에요.");
		Room mockRoom1 = createMockRoom(101L, "1A", 2, "남성", 300000, 5000000, LocalDate.of(2024,12,31), "100000");
		Room mockRoom2 = createMockRoom(102L, "2A", 1, "여성", 200000, 3000000, LocalDate.of(2025,01,17), "50000");

		Roommate roommate1 = createMockRoommate(25, "학생", "INTJ", "23:00-24:00", "09:00-23:00");
		Roommate roommate2 = createMockRoommate(28, "디자이너", "ENFP", "22:00-23:00", "08:00-22:00");

		mockRoom1.setRoommates(List.of(roommate1));
		mockRoom2.setRoommates(List.of(roommate2));
		mockHouse.setRooms(List.of(mockRoom1, mockRoom2));

		Pin mockPin = createMockPin(userId, mockHouse);
		mockHouse.setPins(List.of(mockPin));

		when(houseRepository.findHouseDetailsById(houseId)).thenReturn(Optional.of(mockHouse));

		// When: 서비스 호출
		HouseDetailsResponseDto response = houseService.getHouseDetails(houseId, userId);

		// Then: 검증
		assertNotNull(response); // 응답이 null이 아님

		// HouseInfoDto 검증
		HouseDetailsResponseDto.HouseInfoDto houseInfo = response.houseInfo();
		assertEquals(houseId, houseInfo.houseId());
		assertEquals("루미 100호점(이대역)", houseInfo.name());
		assertEquals("서울 강남구", houseInfo.location());
		assertEquals("전반적으로 조용하고 깔끔한 환경을 선호하는 아침형 룸메이트들이에요.", houseInfo.roomMood());
		assertEquals(List.of("요리 후 바로 설거지해요","청소는 주3회 돌아가면서 해요"), houseInfo.groundRule());
		assertTrue(houseInfo.isPinned());
		assertEquals(List.of("소화기", "비상구"), houseInfo.safetyLivingFacility());
		assertEquals(List.of("냉장고", "전자레인지"), houseInfo.kitchenFacility());

		// RoomDto 검증
		assertEquals(2, response.rooms().size());
		HouseDetailsResponseDto.RoomDto roomDto1 = response.rooms().get(0);
		assertEquals(101L, roomDto1.roomId());
		assertEquals("1A", roomDto1.name());
		assertFalse(roomDto1.status());
		assertEquals(2, roomDto1.occupancyType());
		assertEquals("남성", roomDto1.gender());
		assertEquals(300000, roomDto1.monthlyRent());
		assertEquals(5000000, roomDto1.deposit());
		assertEquals("24-12-31", roomDto1.contractPeriod().format(formatter));
		assertEquals("100000", roomDto1.managementFee());

		// RoommateDto 검증
		assertEquals(2, response.roommates().size());
		HouseDetailsResponseDto.RoommateDto roommateDto1 = response.roommates().get(0);
		assertEquals("1A", roommateDto1.name());
		assertEquals(25, roommateDto1.age());
		assertEquals("학생", roommateDto1.job());
		assertEquals("INTJ", roommateDto1.mbti());
		assertEquals("23:00-24:00", roommateDto1.sleepTime());
		assertEquals("09:00-23:00", roommateDto1.activityTime());
	}

	private House createMockHouse(Long houseId, String name, String location, String roomMood) {
		House house = new House();
		house.setId(houseId);
		house.setName(name);
		house.setLocation(location);
		house.setRoomMood(roomMood);
		house.setGroundRule("요리 후 바로 설거지해요#청소는 주3회 돌아가면서 해요");
		house.setSafetyLivingFacility("소화기#비상구");
		house.setKitchenFacility("냉장고#전자레인지");
		house.setGenderPolicyType(GenderPolicyType.남녀공용);
		return house;
	}
	private Room createMockRoom(Long roomId, String name, int occupancyType, String gender, int monthlyRent,
								int deposit, LocalDate contractPeriod, String managementFee) {
		Room room = new Room();
		room.setId(roomId);
		room.setName(name);
		room.setOccupancyType(occupancyType);
		room.setGenderType(GenderType.valueOf(gender));
		room.setMonthlyRent(monthlyRent);
		room.setDeposit(deposit);
		room.setContractPeriod(contractPeriod);
		room.setManagementFee(managementFee);
		room.setStatus(occupancyType);
		return room;
	}
	private Roommate createMockRoommate(int age, String job, String mbti, String sleepTime, String activityTime) {
		Roommate roommate = new Roommate();
		roommate.setAge(age);
		roommate.setJob(job);
		roommate.setMbti(mbti);
		roommate.setSleepTime(sleepTime);
		roommate.setActivateTime(activityTime);
		return roommate;
	}
	private Pin createMockPin(Long userId, House house) {
		Pin pin = new Pin();
		pin.setId(1L);
		pin.setHouse(house);

		User user = new User();
		user.setId(userId);
		pin.setUser(user);

		return pin;
	}

	@Test
	void testTogglePin_AddPin() {
		// Given
		Long userId = 1L;
		Long houseId = 2L;

		User mockUser = new User();
		mockUser.setId(userId);

		House mockHouse = new House();
		mockHouse.setId(houseId);

		when(pinRepository.findByUserIdAndHouseId(userId, houseId)).thenReturn(Optional.empty());
		when(userRepository.getReferenceById(userId)).thenReturn(mockUser);
		when(houseRepository.getReferenceById(houseId)).thenReturn(mockHouse);

		// When
		boolean result = houseService.togglePin(userId, houseId);

		// Then
		assertTrue(result); // 핀이 추가되므로 true 반환
		verify(pinRepository, times(1)).save(argThat(pin ->
				pin.getUser().equals(mockUser) &&
						pin.getHouse().equals(mockHouse)
		));
		verify(pinRepository, never()).deleteByUserIdAndHouseId(userId, houseId); // 삭제는 호출되지 않아야 함
	}

	@Test
	void testTogglePin_RemovePin() {
		// Given
		Long userId = 1L;
		Long houseId = 2L;

		Pin existingPin = new Pin();
		existingPin.setId(1L);

		when(pinRepository.findByUserIdAndHouseId(userId, houseId)).thenReturn(Optional.of(existingPin));

		// When
		boolean result = houseService.togglePin(userId, houseId);

		// Then
		assertFalse(result); // 핀이 삭제되므로 false 반환
		verify(pinRepository, times(1)).deleteByUserIdAndHouseId(userId, houseId); // 삭제 메서드 호출 확인
		verify(pinRepository, never()).save(any(Pin.class)); // 저장은 호출되지 않아야 함
	}

	@Test
	void testGetHouseImages_Success() {
		// Given
		Long houseId = 1L;

		// Mock House 객체 생성
		House mockHouse = new House();
		mockHouse.setId(houseId);
		mockHouse.setMainImgUrl("https://example.com/main.jpg");
		mockHouse.setMainImgDescription("메인 이미지 상세설명");
		mockHouse.setFacilityImgUrl("https://example.com/facility1.jpg https://example.com/facility2.jpg");
		mockHouse.setFacilityImgDescription("시설 이미지 상세설명");
		mockHouse.setFloorImgUrl("https://example.com/floor.jpg");

		when(houseRepository.findById(houseId)).thenReturn(Optional.of(mockHouse));

		// When
		ImageDetailsResponseDto response = houseService.getHouseImages(houseId);

		// Then
		assertNotNull(response); // 응답이 null이 아님
		assertNotNull(response.images()); // images 필드가 null이 아님

		ImageDetailsResponseDto.Images images = response.images();
		assertEquals("https://example.com/main.jpg", images.mainImgUrl());
		assertEquals("메인 이미지 상세설명", images.mainImgDescription());
		assertEquals(
				List.of("https://example.com/facility1.jpg", "https://example.com/facility2.jpg"),
				images.facilityImgUrls()
		);
		assertEquals("시설 이미지 상세설명", images.facilityImgDescription());
		assertEquals("https://example.com/floor.jpg", images.floorImgUrl());

		// Verify Repository 호출 확인
		verify(houseRepository, times(1)).findById(houseId);
	}

	@Test
	void testGetHouseImages_HouseNotFound() {
		// Given
		Long houseId = 1L;

		when(houseRepository.findById(houseId)).thenReturn(Optional.empty());

		// When & Then
		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
			houseService.getHouseImages(houseId);
		});

		assertEquals("House not found.", exception.getMessage());

		// Verify Repository 호출 확인
		verify(houseRepository, times(1)).findById(houseId);
	}

	@Test
	void testGetHouseRooms() {
		// Given: Mock 데이터 생성
		Room mockRoom1 = createMockRoom(1L, "1A", "책상#침대#옷장", 2, 1, "https://example.com/room1.jpg https://example.com/room1-2.jpg");
		Room mockRoom2 = createMockRoom(2L, "2A", "책상#침대#의자", 1, 1, "https://example.com/room2.jpg");

		List<Room> mockRooms = List.of(mockRoom1, mockRoom2);

		// When: 변환 로직 호출
		List<RoomDetailsResponseDto.Room> roomDtos = mockRooms.stream()
				.sorted(Comparator.comparing(Room::getId))
				.map(room -> RoomDetailsResponseDto.Room.builder()
						.roomId(room.getId())
						.name(room.getName())
						.facility(Arrays.asList(room.getFacility().split("#")))
						.status(room.getStatus() != room.getOccupancyType())
						.mainImageUrl(Arrays.asList(room.getMainImgUrl().split(" ")))
						.build())
				.toList();

		// Then: 변환 결과 검증
		assertEquals(2, roomDtos.size());

		// Room 1 검증
		RoomDetailsResponseDto.Room roomDto1 = roomDtos.get(0);
		assertEquals(1L, roomDto1.roomId());
		assertEquals("1A", roomDto1.name());
		assertEquals(List.of("책상", "침대", "옷장"), roomDto1.facility());
		assertTrue(roomDto1.status());
		assertEquals(List.of("https://example.com/room1.jpg", "https://example.com/room1-2.jpg"), roomDto1.mainImageUrl());

		// Room 2 검증
		RoomDetailsResponseDto.Room roomDto2 = roomDtos.get(1);
		assertEquals(2L, roomDto2.roomId());
		assertEquals("2A", roomDto2.name());
		assertEquals(List.of("책상", "침대", "의자"), roomDto2.facility());
		assertFalse(roomDto2.status());
		assertEquals(List.of("https://example.com/room2.jpg"), roomDto2.mainImageUrl());
	}

	private Room createMockRoom(Long roomId, String name, String facility, int occupancyType, int status, String mainImgUrl) {
		Room room = new Room();
		room.setId(roomId);
		room.setName(name);
		room.setFacility(facility);
		room.setOccupancyType(occupancyType);
		room.setStatus(status);
		room.setMainImgUrl(mainImgUrl);
		return room;
	}
}
