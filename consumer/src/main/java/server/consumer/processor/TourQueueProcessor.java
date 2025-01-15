package server.consumer.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.TourRequestDto;
import entity.HousingRequest;
import entity.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import server.consumer.repository.RoomRepository;
import server.consumer.repository.TourRequestRepository;

@Service
@RequiredArgsConstructor
public class TourQueueProcessor {
	private final StringRedisTemplate redisTemplate;
	private final ObjectMapper objectMapper;
	private final TourRequestRepository tourRequestRepository;
	private final RoomRepository roomRepository;

	public void processMessage(String queueName) {
		String jsonMessage = redisTemplate.opsForList().leftPop(queueName);
		if (jsonMessage != null) {
			try {
				TourRequestDto tourRequestDto = objectMapper.readValue(jsonMessage, TourRequestDto.class);
				System.out.println("Processing message: " + tourRequestDto); // 나중에 로그 추가하기

				/*
				메세지 처리 로직
				 */
				Room room = roomRepository.getReferenceById(tourRequestDto.roomId());
				HousingRequest housingRequest = new HousingRequest(
						tourRequestDto.name(),
						tourRequestDto.birth(),
						tourRequestDto.gender(),
						tourRequestDto.phoneNumber(),
						tourRequestDto.preferredDate(),
						tourRequestDto.message(),
						room
						);
				tourRequestRepository.save(housingRequest);
				/*
				카카오톡 API 나중에 선택적으로 개발
				 */
				System.out.println(housingRequest);
			} catch (JsonProcessingException e) {
				throw new RuntimeException("Failed to deserialize message", e);
			}
		} else {
			System.out.println("No messages in the queue.");
		}
	}
}