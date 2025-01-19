package server.producer.external.service.redis;

import entity.History;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import server.producer.external.repository.HistoryRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisSender {
	private final StringRedisTemplate redisTemplate;
	private final HistoryRepository historyRepository; // Repo 이름 수정: `HisotryRepo` -> `HistoryRepo`

	public void send(String queueName, String message) {
		try {
			// 메시지를 Redis 큐에 추가
			redisTemplate.opsForList().rightPush(queueName, message);
			log.info("Message successfully pushed to queue [{}]: {}", queueName, message);
		} catch (Exception e) {
			// 예외 발생 시 DB에 기록
			log.error("Failed to push message to queue [{}]: {}", queueName, e.getMessage(), e);
			History history = new History();
			history.setBody("Failed to send message: " + message);
			historyRepository.save(history);
		}
	}
}
