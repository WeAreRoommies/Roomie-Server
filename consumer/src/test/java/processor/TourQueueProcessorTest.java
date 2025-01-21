package processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.TourRequestDto;
import entity.GenderType;
import entity.HousingRequest;
import entity.Room;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import server.consumer.ConsumerApplication;
import server.consumer.processor.TourQueueProcessor;
import server.consumer.repository.HouseRepository;
import server.consumer.repository.RoomRepository;
import server.consumer.repository.TourRequestRepository;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ConsumerApplication.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TourQueueProcessorTest {

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private RoomRepository roomRepository;

	@Autowired
	private HouseRepository hourRepository;

	@Autowired
	private TourRequestRepository tourRequestRepository;

	@Autowired
	private TourQueueProcessor tourQueueProcessor;

	private final String queueName = "tour-queue";
	@Autowired
	private HouseRepository houseRepository;

	@BeforeEach
	void setUp() throws Exception {
		// Redis 초기화
		redisTemplate.delete(queueName);
		tourRequestRepository.deleteAll();
		roomRepository.deleteAll();
		houseRepository.deleteAll();

		// Room 엔티티 저장
		Room room = new Room();
		room.setId(1L); // 외래 키 참조를 만족하기 위해 ID 설정
		room.setName("Test Room");
		room.setGender(GenderType.남성);
		roomRepository.save(room);
	}

//	@Test
	@Transactional
	void testProcessMessageWithRedis() throws Exception {
		// Arrange
		TourRequestDto tourRequestDto = createTourMessage();
		String jsonMessage = objectMapper.writeValueAsString(tourRequestDto);

		// Redis에 메시지 추가
		redisTemplate.opsForList().rightPush(queueName, jsonMessage);

		// Act
		tourQueueProcessor.processMessage(queueName);

		// Assert
		HousingRequest housingRequest = tourRequestRepository.findAll().get(0);

		assertNotNull(housingRequest);
		assertEquals(tourRequestDto.name(), housingRequest.getName());
		assertEquals(tourRequestDto.birth(), housingRequest.getBirth());
		assertEquals(tourRequestDto.gender(), housingRequest.getGender());
		assertEquals(tourRequestDto.phoneNumber(), housingRequest.getPhoneNumber());
		assertEquals(tourRequestDto.preferredDate(), housingRequest.getPreferredDate());
		assertEquals(tourRequestDto.message(), housingRequest.getMessage());
		assertEquals(1L, housingRequest.getRoom().getId()); // 실제 저장된 Room ID 확인
	}

	private TourRequestDto createTourMessage() {
		return TourRequestDto.builder()
				.name("김루미")
				.birth(LocalDate.of(2002, 3, 5))
				.gender(GenderType.남성.toString())
				.phoneNumber("01012345678")
				.preferredDate(LocalDate.of(2025, 1, 11))
				.message("방 투어 요청합니다.")
				.roomId(1L)
				.houseId(1L)
				.build();
	}
}