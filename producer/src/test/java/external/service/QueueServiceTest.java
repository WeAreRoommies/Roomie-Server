package external.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.TourRequestDto;
import entity.GenderType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import server.producer.ProducerApplication;
import server.producer.external.service.redis.QueueService;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = ProducerApplication.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class QueueServiceTest {

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private QueueService queueService;

	private final String queueName = "testQueue";

	@BeforeEach
	void setUp() {
		redisTemplate.delete(queueName);
	}

	@Test
	void testSendMessage() throws JsonProcessingException {
		// given
		TourRequestDto message = createTourMessage();

		// when
		queueService.sendMessage(queueName, message);

		// then
		// Redis에서 데이터 확인
		String storedMessage = redisTemplate.opsForList().leftPop(queueName);
		assertNotNull(storedMessage, "Message should be stored in Redis");

		// JSON 역직렬화로 데이터 검증
		TourRequestDto deserializedMessage = objectMapper.readValue(storedMessage, TourRequestDto.class);
		assertEquals(message, deserializedMessage);
	}

	TourRequestDto createTourMessage(){
		TourRequestDto message = TourRequestDto.builder()
				.name("김루미")
				.birth(LocalDate.of(2002,3,5))
				.gender(GenderType.남성.toString())
				.phoneNumber("01012345678")
				.preferredDate(LocalDate.of(2025,1,11))
				.message("당신의 방에 관심이 많습니다")
				.roomId(1L)
				.houseId(1L).build();
		return message;
	}
}