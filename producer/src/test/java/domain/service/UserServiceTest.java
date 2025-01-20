package domain.service;

import entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import server.producer.domain.dto.response.HomeInfoResponseDto;
import server.producer.domain.repository.HouseRepository;
import server.producer.domain.repository.UserRepository;
import server.producer.domain.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import entity.User;
import server.producer.domain.dto.response.MyPageResponseDto;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

	@Mock
	private HouseRepository houseRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @InjectMocks
    private UserService userService;

    @Test
    void testGetMyPage() {
        // Given: Mock 데이터 설정
        Long userId = 1L;
        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setName("홍길동");

        // userRepository의 동작 Mock 설정
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        // When: 서비스 호출
        MyPageResponseDto response = userService.getMyPage(userId);

        // Then: 변환 결과 검증
        assertNotNull(response); // 응답이 null이 아님
        assertEquals("홍길동", response.name()); // 이름 필드가 기대값과 일치
    }

	@Test
	void getUserInfoAndRecentlyViewedHouse(){
		//given
		Long userId = 1L;
		User user = createUserWithHousesAndRooms(userId);

		when(userRepository.findUserWithRecentlyViewedHouses(anyLong()))
				.thenReturn(Optional.of(user));
		when(houseRepository.findRecentlyViewedHousesByUserId(user.getId()))
				.thenReturn(user.getRecentlyViewedHouses().stream().map(RecentlyViewedHouse::getHouse).toList());
		//when
		HomeInfoResponseDto result = userService.getUserInfoAndRecentlyViewedHouse(userId);
		HomeInfoResponseDto.RecentlyViewedHouseDto resultHouse = result.recentlyViewedHouses().get(0);

		// then
		assertEquals("유저동", result.location());
		assertEquals(2, result.recentlyViewedHouses().size());
		assertEquals(1L, resultHouse.houseId());
		assertEquals("서대문구 대현동", resultHouse.location());
		assertEquals(12, resultHouse.contractTerm());
		assertEquals("https://example.com/images/house" + 1 + ".jpg", resultHouse.mainImgUrl());
		assertEquals("#차분한", resultHouse.moodTag());
	}

	private User createUserWithHousesAndRooms(Long userId) {
		// User 생성
		User user = new User();
		user.setId(userId);
		user.setName("Test User " + userId);
		user.setLocation("서대문구 유저동");

		// RecentlyViewedHouse 리스트 생성
		List<RecentlyViewedHouse> recentlyViewedHouses = new ArrayList<>();

		for (long houseId = 1; houseId <= 2; houseId++) { // 두 개의 House 생성
			// House 생성
			House house = new House();
			house.setId(houseId);
			house.setName("Sample House " + houseId);
			house.setLatitude(37.5665 + houseId * 0.001); // 예제 좌표
			house.setLongitude(126.978 + houseId * 0.001); // 예제 좌표
			house.setRoomMood("Cozy");
			house.setGroundRule("No smoking");
			house.setLocation("서대문구 대현동");
			house.setLocationDescription("자이아파트 " + houseId);
			house.setMoodTag("#차분한");
			house.setSubMoodTag("#추가태그");
			house.setGenderPolicy(GenderPolicyType.남녀공용);
			house.setMainImgUrl("https://example.com/images/house" + houseId + ".jpg");
			house.setMainImgDescription("Main image of house " + houseId);
			house.setFacilityImgUrl("https://example.com/images/facility" + houseId + ".jpg");
			house.setFacilityImgDescription("Facility image of house " + houseId);
			house.setContractTerm(12);
			house.setSafetyLivingFacility("소화기");
			house.setKitchenFacility("냉장고");

			// Room 리스트 생성
			List<Room> rooms = new ArrayList<>();
			for (int i = 1; i <= 3; i++) { // 각 House에 3개의 Room 생성
				Room room = new Room();
				room.setId(houseId * 10 + i); // 유니크한 Room ID
				room.setHouse(house);
				room.setName("Room " + i);
				room.setMonthlyRent(300000 + i * 50000);
				room.setDeposit(1000000 + i * 100000);
				room.setOccupancyType(i % 2 == 0 ? 2 : 1); // 1인실 또는 2인실
				room.setStatus(1); // 활성 상태
				room.setMainImgUrl("https://example.com/images/room" + (houseId * 10 + i) + ".jpg");
				room.setPrepaidUtilities(50000);
				room.setManagementFee("100000");
				rooms.add(room);
			}
			house.setRooms(rooms);

			// RecentlyViewedHouse 생성
			RecentlyViewedHouse recentlyViewedHouse = new RecentlyViewedHouse();
			recentlyViewedHouse.setId(houseId);
			recentlyViewedHouse.setViewedAt(LocalDateTime.now().minusDays(houseId)); // 최근 조회 날짜
			recentlyViewedHouse.setUser(user);
			recentlyViewedHouse.setHouse(house);

			recentlyViewedHouses.add(recentlyViewedHouse);
		}

		// User에 RecentlyViewedHouse 리스트 추가
		user.setRecentlyViewedHouses(recentlyViewedHouses);

		return user;
	}
}
