package server.consumer.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.TourRequestDto;
import entity.History;
import entity.HousingRequest;
import entity.Room;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import server.consumer.repository.HistoryRepository;
import server.consumer.repository.RoomRepository;
import server.consumer.repository.TourRequestRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class TourQueueProcessor {
	private final StringRedisTemplate redisTemplate;
	private final ObjectMapper objectMapper;
	private final TourRequestRepository tourRequestRepository;
	private final RoomRepository roomRepository;
	private final HistoryRepository historyRepository;

	public void processMessage(String queueName) {
		String jsonMessage = redisTemplate.opsForList().leftPop(queueName);
		if (jsonMessage != null) {
			try {
				TourRequestDto tourRequestDto = objectMapper.readValue(jsonMessage, TourRequestDto.class);
				log.info("Processing message: " + tourRequestDto);
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
				log.info(housingRequest.toString());
			} catch (JsonProcessingException e) {
				History history = new History();
				history.setBody(jsonMessage);
				historyRepository.save(history);
			}
			log.info("No messages in the queue.");
		}
	}
}