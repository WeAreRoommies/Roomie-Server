package domain.repository;

import entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ContextConfiguration;
import server.producer.ProducerApplication;
import server.producer.domain.repository.HouseRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ContextConfiguration(classes = ProducerApplication.class)
@EntityScan(basePackages = "entity")
public class HouseRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private HouseRepository houseRepository;

    @Test
    void testFindHouseDetailsById_ValidHouseId() {
        // Given
        House house = new House();
        house.setName("루미 100호점");
        house.setLatitude(37.5665);
        house.setLongitude(126.9780);
        house.setLocation("마포구 상수동");
        house.setLocationDescription("자이아파트");
        house.setMoodTag("#아늑한");
        house.setSubMoodTag("#즐거운 #활기찬");
        house.setRoomMood("조용하고 깔끔한 환경을 선호합니다.");
        house.setMainImgUrl("house_main.jpg");
        house.setMainImgDescription("메인이미지 상세설명");
        house.setFacilityImgUrl("facility_img.jpg");
        house.setFacilityImgDescription("시설이미지 상세설명");
        house.setFloorImgUrl("floor_plan.jpg");
        house.setContractTerm(12);
        house.setGenderPolicy(GenderPolicyType.남녀공용);
        house.setGroundRule("담배 피지 말기#변기 막히지 말기");
        house.setKitchenFacility("냉장고#싱크대");
        house.setSafetyLivingFacility("소화기#티비");

        Room room = new Room();
        room.setName("Room 1");
        room.setMonthlyRent(500000);
        room.setDeposit(5000000);
        room.setFacility("에어컨#와이파이");
        room.setContractPeriod(LocalDate.of(2025, 12, 31));
        room.setGender(GenderType.혼성);
        room.setStatus(1);
        room.setOccupancyType(2);
        room.setMainImgUrl("room_main.jpg");
        room.setManagementFee("1/n");
        room.setHouse(house);

        Pin pin = new Pin();
        pin.setHouse(house);

        house.getRooms().add(room);
        house.getPins().add(pin);

        // 저장
        entityManager.persist(house);
        entityManager.flush();

        // When
        Optional<House> houseWithRooms = houseRepository.findHouseWithRoomsById(house.getId());
        List<Room> roomsWithOccupancies = houseRepository.findRoomsAndRoomOccupanciesByHouseId(house.getId());
        Optional<House> houseWithPins = houseRepository.findHouseWithPinsById(house.getId());

        // Then
        assertTrue(houseWithRooms.isPresent());
        assertTrue(houseWithPins.isPresent());
        assertEquals(1, roomsWithOccupancies.size());

        House foundHouse = houseWithRooms.get();
        assertEquals("루미 100호점", foundHouse.getName());
        assertEquals(1, foundHouse.getRooms().size());
        assertEquals("Room 1", foundHouse.getRooms().get(0).getName());
        assertEquals(1, houseWithPins.get().getPins().size());
    }

    @Test
    void testFindHouseDetailsById_InvalidHouseId() {
        // Given
        Long invalidHouseId = 999L;

        // When
        Optional<House> houseWithRooms = houseRepository.findHouseWithRoomsById(invalidHouseId);
        List<Room> roomsWithOccupancies = houseRepository.findRoomsAndRoomOccupanciesByHouseId(invalidHouseId);
        Optional<House> houseWithPins = houseRepository.findHouseWithPinsById(invalidHouseId);

        // Then
        assertTrue(houseWithRooms.isEmpty());
        assertTrue(roomsWithOccupancies.isEmpty());
        assertTrue(houseWithPins.isEmpty());
    }

    @Test
    void testFindPinnedHouseByUserId() {
        // Given: Mock 데이터 생성
        Long userId = 1L;

        // 사용자 생성 및 영속화
        User user = new User();
        user.setId(userId);
        user.setName("Test User");
        user.setLocation("서울");
        user = entityManager.merge(user);

        // 하우스 생성
        House house1 = new House();
        house1.setName("Test House 1");
        house1.setLocation("서울 강남구");
        house1.setLocationDescription("강남아파트");
        house1.setFacilityImgUrl("facility_img_url_1.jpg");
        house1.setFacilityImgDescription("Facility description 1");
        house1.setMainImgUrl("main_img_url_1.jpg");
        house1.setMainImgDescription("Main img description 1");
        house1.setLatitude(37.5665);
        house1.setLongitude(126.9780);
        house1.setMoodTag("#편안한");
        house1.setSubMoodTag("#활기찬");
        house1.setRoomMood("아늑한 방");
        house1.setFloorImgUrl("floor_img_1.jpg");
        house1.setContractTerm(12);
        house1.setGenderPolicy(GenderPolicyType.남녀공용);
        house1.setGroundRule("금연");
        house1.setKitchenFacility("싱크대#냉장고");
        house1.setSafetyLivingFacility("소화기#응급키트");

        House house2 = new House();
        house2.setName("Test House 2");
        house2.setLocation("서울 마포구");
        house2.setLocationDescription("레미안 아파트");
        house2.setFacilityImgUrl("facility_img_url_2.jpg");
        house2.setFacilityImgDescription("Facility description 2");
        house2.setMainImgUrl("main_img_url_2.jpg");
        house2.setMainImgDescription("Main img description 2");
        house2.setLatitude(37.5665);
        house2.setLongitude(126.9780);
        house2.setMoodTag("#활기찬");
        house2.setSubMoodTag("#편안한");
        house2.setRoomMood("활기찬 방");
        house2.setFloorImgUrl("floor_img_2.jpg");
        house2.setContractTerm(6);
        house2.setGenderPolicy(GenderPolicyType.남녀공용);
        house2.setGroundRule("소음 금지");
        house2.setKitchenFacility("전자레인지#가스레인지");
        house2.setSafetyLivingFacility("CCTV#소화기");

        // 핀 생성 및 사용자와 연결
        Pin pin1 = new Pin();
        pin1.setHouse(house1);
        pin1.setUser(user);

        Pin pin2 = new Pin();
        pin2.setHouse(house2);
        pin2.setUser(user);

        house1.getPins().add(pin1);
        house2.getPins().add(pin2);

        // 데이터 저장
        entityManager.persist(house1);
        entityManager.persist(house2);
        entityManager.flush();

        // When: findPinnedHouseByUserId 호출
        List<House> pinnedHouses = houseRepository.findPinnedHouseByUserId(userId);

        // Then: 결과 검증
        assertNotNull(pinnedHouses);
        assertEquals(2, pinnedHouses.size());

        House foundHouse1 = pinnedHouses.get(0);
        House foundHouse2 = pinnedHouses.get(1);

        assertEquals("Test House 1", foundHouse1.getName());
        assertEquals("서울 강남구", foundHouse1.getLocation());
        assertEquals(1, foundHouse1.getPins().size());
        assertEquals(userId, foundHouse1.getPins().get(0).getUser().getId());

        assertEquals("Test House 2", foundHouse2.getName());
        assertEquals("서울 마포구", foundHouse2.getLocation());
        assertEquals(1, foundHouse2.getPins().size());
        assertEquals(userId, foundHouse2.getPins().get(0).getUser().getId());
    }

    @Test
    void testFindAllRoomsByHouseId() {
        // Given: 테스트용 House와 Room 데이터 생성
        House house = new House();
        house.setName("Test House");
        house.setLocation("서울 강남구");
        house.setLocationDescription("강남구 중심부");
        house.setFacilityImgUrl("facility_img_url.jpg");
        house.setFacilityImgDescription("시설 이미지 설명");
        house.setMainImgUrl("main_img_url.jpg");
        house.setMainImgDescription("메인 이미지 설명");
        house.setLatitude(37.5665);
        house.setLongitude(126.9780);
        house.setMoodTag("#편안한");
        house.setSubMoodTag("#활기찬");
        house.setRoomMood("조용한 방");
        house.setFloorImgUrl("floor_img_url.jpg");
        house.setContractTerm(12);
        house.setGenderPolicy(GenderPolicyType.남녀공용);
        house.setGroundRule("금연");
        house.setKitchenFacility("싱크대, 냉장고");
        house.setSafetyLivingFacility("CCTV, 소화기");

        Room room1 = new Room();
        room1.setName("Room 1");
        room1.setMonthlyRent(500000);
        room1.setDeposit(5000000);
        room1.setFacility("에어컨, 와이파이");
        room1.setContractPeriod(LocalDate.of(2025, 12, 31));
        room1.setGender(GenderType.혼성);
        room1.setStatus(1);
        room1.setOccupancyType(2);
        room1.setMainImgUrl("room1_main.jpg");
        room1.setManagementFee("1/n");
        room1.setHouse(house);

        Room room2 = new Room();
        room2.setName("Room 2");
        room2.setMonthlyRent(700000);
        room2.setDeposit(7000000);
        room2.setFacility("히터, 책상");
        room2.setContractPeriod(LocalDate.of(2026, 12, 31));
        room2.setGender(GenderType.혼성);
        room2.setStatus(1);
        room2.setOccupancyType(1);
        room2.setMainImgUrl("room2_main.jpg");
        room2.setManagementFee("2/n");
        room2.setHouse(house);

        house.getRooms().add(room1);
        house.getRooms().add(room2);

        // House와 Room 영속화
        entityManager.persist(house);
        entityManager.persist(room1);
        entityManager.persist(room2);
        entityManager.flush();

        // When: findAllRoomsByHouseId 호출
        List<Room> rooms = houseRepository.findAllRoomsByHouseId(house.getId());

        // Then: 조회 결과 검증
        assertNotNull(rooms);
        assertEquals(2, rooms.size());

        Room foundRoom1 = rooms.get(0);
        Room foundRoom2 = rooms.get(1);

        assertEquals("Room 1", foundRoom1.getName());
        assertEquals(500000, foundRoom1.getMonthlyRent());
        assertEquals("에어컨, 와이파이", foundRoom1.getFacility());
        assertEquals("room1_main.jpg", foundRoom1.getMainImgUrl());

        assertEquals("Room 2", foundRoom2.getName());
        assertEquals(700000, foundRoom2.getMonthlyRent());
        assertEquals("히터, 책상", foundRoom2.getFacility());
        assertEquals("room2_main.jpg", foundRoom2.getMainImgUrl());
    }
}
