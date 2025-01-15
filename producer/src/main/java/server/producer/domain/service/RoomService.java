package server.producer.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import dto.TourRequestDto;
import server.producer.external.service.redis.QueueService;

@Service
@RequiredArgsConstructor
public class RoomService {
	private final QueueService queueService;

	public void createRoomTourRequest(TourRequestDto requestDto){
		queueService.sendMessage("tourRequest", requestDto);
	}
}
