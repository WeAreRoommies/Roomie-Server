package server.consumer.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import server.consumer.processor.TourQueueProcessor;

@Component
@RequiredArgsConstructor
@Slf4j
public class TourQueueScheduler {

	private final TourQueueProcessor tourQueueProcessor;

	@Scheduled(fixedDelay = 3000)
	public void consumeMessages() {
		String queueName = "tourRequest";
		log.info("Consuming messages from queue: {}", queueName);
		tourQueueProcessor.processMessage(queueName);
	}
}