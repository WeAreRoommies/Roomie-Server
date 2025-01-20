package server.producer.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import dto.TourRequestDto;
import server.producer.external.service.redis.QueueService;

import java.security.InvalidParameterException;

@Service
@RequiredArgsConstructor
public class RoomService {
	private final QueueService queueService;

	public void createRoomTourRequest(TourRequestDto requestDto){
		if (requestDto == null) {
			throw new InvalidParameterException("TourRequestDto is invalid: " + requestDto);
		}
		queueService.sendMessage("tourRequest", requestDto);
	}
}
