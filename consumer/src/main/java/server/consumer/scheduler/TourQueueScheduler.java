package server.consumer.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import server.consumer.processor.TourQueueProcessor;

@Component
@RequiredArgsConstructor
public class TourQueueScheduler {

	private final TourQueueProcessor tourQueueProcessor;

	@Scheduled(fixedDelay = 3000)
	public void consumeMessages() {
		String queueName = "tourRequest";
		tourQueueProcessor.processMessage(queueName);
	}
}