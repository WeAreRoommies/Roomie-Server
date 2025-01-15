package server.producer.external.service.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import dto.TourRequestDto;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TourQueueService {
	private final StringRedisTemplate redisTemplate;
	private final ObjectMapper objectMapper;

	public void sendMessage(String queueName, TourRequestDto message) {
		try {
			// DTO를 JSON 문자열로 직렬화
			String jsonMessage = objectMapper.writeValueAsString(message);
			redisTemplate.opsForList().rightPush(queueName, jsonMessage);
//			System.out.println("Message added to queue: " + jsonMessage); 나중에 로그
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Failed to serialize message", e);
		}
	}
}
