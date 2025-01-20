package server.producer.external.service.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.History;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import dto.TourRequestDto;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import server.producer.external.repository.HistoryRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueService {
	private final ObjectMapper objectMapper;
	private final RedisSender redisSender;

	public void sendMessage(String queueName, TourRequestDto message) {
		try {
			// DTO를 JSON 문자열로 직렬화
			String jsonMessage = objectMapper.writeValueAsString(message);
			redisSender.send(queueName, jsonMessage);
			log.info("Message added to queue [{}]: {}", queueName, jsonMessage);
		} catch (JsonProcessingException e) {
			log.error("Failed to serialize message: {}", e.getMessage(), e);
			throw new RuntimeException("Failed to serialize message", e);
		} catch (Exception e) {
			log.error("Unexpected error occurred while adding message to queue: {}", e.getMessage(), e);
			throw new RuntimeException("Unexpected error occurred", e);
		}
	}
}

