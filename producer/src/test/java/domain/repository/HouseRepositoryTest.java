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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        house.setGenderPolicyType(GenderPolicyType.남녀공용);
        house.setGroundRule("담배 피지 말기#변기 막히지 말기");
        house.setKitchenFacility("냉장고#싱크대");
        house.setSafetyLivingFacility("소화기#티비");

        Room room = new Room();
        room.setName("Room 1");
        room.setMonthlyRent(500000);
        room.setDeposit(5000000);
        room.setFacility("에어컨#와이파이");
        room.setContractPeriod(LocalDate.of(2025, 12, 31));
        room.setGenderType(GenderType.혼성);
        room.setStatus(1);
        room.setOccupancyType(2);
        room.setMainImgUrl("room_main.jpg");
        room.setPrepaidUtilities(100000);
        room.setManagementFee("1/n");
        room.setHouse(house);

        Roommate roommate = new Roommate();
        roommate.setAge(25);
        roommate.setJob("개발자");
        roommate.setMbti("INTJ");
        roommate.setSleepTime("23:00-24:00");
        roommate.setActivateTime("09:00-10:00");
        roommate.setRoom(room);

        Pin pin = new Pin();
        pin.setHouse(house);

        house.getRooms().add(room);
        room.getRoommates().add(roommate);
        house.getPins().add(pin);

        // 저장
        entityManager.persist(house);
        entityManager.flush();

        // When
        Optional<House> houseWithRooms = houseRepository.findHouseWithRoomsById(house.getId());
        List<Room> roomsWithRoommates = houseRepository.findRoomsAndRoommatesByHouseId(house.getId());
        Optional<House> houseWithPins = houseRepository.findHouseWithPinsById(house.getId());

        // Then
        assertTrue(houseWithRooms.isPresent());
        assertTrue(houseWithPins.isPresent());
        assertEquals(1, roomsWithRoommates.size());

        House foundHouse = houseWithRooms.get();
        assertEquals("루미 100호점", foundHouse.getName());
        assertEquals(1, foundHouse.getRooms().size());
        assertEquals("Room 1", foundHouse.getRooms().get(0).getName());
        assertEquals(1, foundHouse.getRooms().get(0).getRoommates().size());
        assertEquals("개발자", foundHouse.getRooms().get(0).getRoommates().get(0).getJob());
        assertEquals(1, houseWithPins.get().getPins().size());
    }

    @Test
    void testFindHouseDetailsById_InvalidHouseId() {
        // Given
        Long invalidHouseId = 999L;

        // When
        Optional<House> houseWithRooms = houseRepository.findHouseWithRoomsById(invalidHouseId);
        List<Room> roomsWithRoommates = houseRepository.findRoomsAndRoommatesByHouseId(invalidHouseId);
        Optional<House> houseWithPins = houseRepository.findHouseWithPinsById(invalidHouseId);

        // Then
        assertTrue(houseWithRooms.isEmpty());
        assertTrue(roomsWithRoommates.isEmpty());
        assertTrue(houseWithPins.isEmpty());
    }
}
