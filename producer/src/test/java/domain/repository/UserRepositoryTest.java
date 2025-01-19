package domain.repository;

import entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import server.producer.ProducerApplication;
import server.producer.domain.repository.HouseRepository;
import server.producer.domain.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ContextConfiguration(classes = ProducerApplication.class)
@EntityScan(basePackages = "entity")
public class UserRepositoryTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private EntityManager em;

	Long testUserId = 1L;

	@BeforeEach
	void setUp() {
		// 사용자 생성 및 저장
		User user1 = createUser("John Doe", "서대문구 대현동");
		User user2 = createUser("Jane Doe", "강남구 역삼동");

		// 하우스와 방 생성 및 저장
		House house = createHouse("Dream House", "Gangnam", "Near the station", "Modern", "Peaceful");
		Room room = createRoom("Room 101", 500000, 5000000, GenderType.남성, house);

		// 최근 본 하우스 연관 설정 및 저장
		linkRecentlyViewedHouse(user1, house);

		// 저장된 엔티티의 ID를 가져옴
		testUserId = user1.getId();

		// Flush to ensure persistence in each test
		em.flush();
	}

	@Test
	void testFindLocationById_ValidUserId() {
		// given
		Long userId = testUserId;

		// when
		Optional<String> location = userRepository.findLocationById(userId);

		// then
		assertTrue(location.isPresent());
		assertEquals("서대문구 대현동", location.get());
	}

	@Test
	void testFindLocationById_InvalidUserId() {
		// given
		Long userId = testUserId + 5;

		// when
		Optional<String> location = userRepository.findLocationById(userId);

		// then
		assertTrue(location.isEmpty());
	}


	@Test
	void testFindByIdWithHousesAndRooms() {
		// Arrange
		Long userId = testUserId;

		// Act
		Optional<User> optionalUser = userRepository.findUserWithRecentlyViewedHouses(userId);

		// Debugging: Optional<User> 상태 확인
		if (optionalUser.isEmpty()) {
			System.out.println("No user found with userId: " + userId);
		}

		// Assert
		assertTrue(optionalUser.isPresent(), "User should be found");

		User user = optionalUser.get();
		System.out.println("User found: " + user.getName());
		assertEquals("John Doe", user.getName());
		assertEquals(1, user.getRecentlyViewedHouses().size(), "User should have 1 recently viewed house");

		RecentlyViewedHouse rvh = user.getRecentlyViewedHouses().get(0);
		assertNotNull(rvh.getHouse(), "Recently viewed house should not be null");
		assertEquals("Dream House", rvh.getHouse().getName(), "House name should match");

		House house = rvh.getHouse();
		assertEquals(1, house.getRooms().size(), "House should have 1 room");

		Room room = house.getRooms().get(0);
		assertEquals("Room 101", room.getName(), "Room name should match");
		assertEquals(500000, room.getMonthlyRent(), "Room monthly rent should match");
	}

	private User createUser(String name, String location) {
		User user = new User();
		user.setName(name);
		user.setLocation(location);
		return userRepository.save(user);
	}

	private House createHouse(String name, String location, String locationDescription, String moodTag, String subMoodTag) {
		House house = new House();
		house.setName(name);
		house.setLatitude(37.5665);
		house.setLongitude(126.9780);
		house.setRoomMood("Cozy");
		house.setGroundRule("No Smoking");
		house.setLocation(location);
		house.setLocationDescription(locationDescription);
		house.setMoodTag(moodTag);
		house.setSubMoodTag(subMoodTag);
		house.setGenderPolicy(GenderPolicyType.남성전용);
		house.setMainImgUrl("https://example.com/main.jpg");
		house.setMainImgDescription("Main Image");
		house.setFacilityImgUrl("https://example.com/facility.jpg");
		house.setFacilityImgDescription("Facility Image");
		house.setContractTerm(12);
		house.setSafetyLivingFacility("CCTV");
		house.setKitchenFacility("Fully Equipped");
		em.persist(house);
		return house;
	}

	private Room createRoom(String name, int monthlyRent, int deposit, GenderType genderType, House house) {
		Room room = new Room();
		room.setName(name);
		room.setMonthlyRent(monthlyRent);
		room.setDeposit(deposit);
		room.setFacility("Bed, Desk");
		room.setGender(genderType);
		room.setStatus(0);
		room.setOccupancyType(1);
		room.setMainImgUrl("https://example.com/room.jpg");
		room.setPrepaidUtilities(50000);
		room.setManagementFee("5000");
		room.setHouse(house);
		house.getRooms().add(room);
		em.persist(room);
		return room;
	}

	private void linkRecentlyViewedHouse(User user, House house) {
		RecentlyViewedHouse recentlyViewedHouse = new RecentlyViewedHouse();
		recentlyViewedHouse.setUser(user);
		recentlyViewedHouse.setHouse(house);
		recentlyViewedHouse.setViewedAt(java.time.LocalDateTime.now());
		house.getRecentlyViewedHouses().add(recentlyViewedHouse);
		user.getRecentlyViewedHouses().add(recentlyViewedHouse);
		em.persist(recentlyViewedHouse); // 명시적으로 persist 호출
	}

}