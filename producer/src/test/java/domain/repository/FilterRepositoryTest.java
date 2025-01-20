package domain.repository;

import entity.*;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import server.producer.ProducerApplication;
import server.producer.domain.dto.request.FilterRequestDto;
import server.producer.domain.repository.FilterRepository;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ContextConfiguration(classes = ProducerApplication.class)
@EntityScan(basePackages = "entity")
@Import(FilterRepository.class)
@Transactional
public class FilterRepositoryTest {

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private FilterRepository filterRepository;

	@BeforeEach
	void setUp() {
		// User 저장
		User user = new User();
		user.setName("Test User");
		user.setLocation("Test Location");
		entityManager.persist(user);

		// House 저장
		House house1 = createHouse(1L, "#차분한", "무슨구 무슨동", "Test Location", true, 12, GenderPolicyType.남성전용);
		House house2 = createHouse(2L, "Cozy", "Seocho", "Relaxing", false, 24, GenderPolicyType.여성전용);

		// Room 저장
		createRoom(101L, "Room 101", 500000, 1300000, null, house1);
		createRoom(201L, "Room 201", 800000, 7000000, null, house2);

		entityManager.flush();
		entityManager.clear();
	}

	@Test
	void testFindFilteredHouses() {
		FilterRequestDto filter = new FilterRequestDto(
		"무슨구 무슨동", // House의 location
				"#차분한", // House의 moodTag
				new FilterRequestDto.Range(1000000, 1500000), // deposit 범위
				new FilterRequestDto.Range(300000, 600000), // monthlyRent 범위
				List.of(GenderPolicyType.남성전용.toString()), // House의 genderPolicyType
				null, // Room의 contractPeriod 관련 조건
				List.of("1인실", "2인실", "3인실", "4인실"), // occupancyTypes (빈 리스트)
				List.of(6,12) // contractTerm 조건
		);

		List<House> filteredHouses = filterRepository.findFilteredHouses(filter);

		assertNotNull(filteredHouses);
		assertEquals(1, filteredHouses.size());
		assertEquals("Sample House 1", filteredHouses.get(0).getName());
	}

	private House createHouse(Long id, String moodTag, String location, String locationDescription,
							  boolean isPinned, int contractTerm, GenderPolicyType genderPolicy) {
		House house = new House();
		house.setName("Sample House " + id);
		house.setLocation(location);
		house.setLatitude(34.3);
		house.setLongitude(33.3);
		house.setMainImgUrl("https://www.example.com");
		house.setMoodTag(moodTag);
		house.setContractTerm(contractTerm);
		house.setGenderPolicy(genderPolicy);

		entityManager.persist(house);

		if (isPinned) {
			User user = entityManager.find(User.class, 1L);
			Pin pin = new Pin();
			pin.setHouse(house);
			pin.setUser(user);
			entityManager.persist(pin);
			house.getPins().add(pin);
			user.getPins().add(pin);
		}

		return house;
	}

	private Room createRoom(Long id, String name, int monthlyRent, int deposit, LocalDate contractDate, House house) {
		Room room = new Room();
		room.setName(name);
		room.setMonthlyRent(monthlyRent);
		room.setDeposit(deposit);
		room.setContractPeriod(contractDate);
		room.setStatus(0);
		room.setOccupancyType(2);
		room.setGender(GenderType.남성);
		room.setHouse(house); // House와 직접 연결
		entityManager.persist(room);
		return room;
	}
}