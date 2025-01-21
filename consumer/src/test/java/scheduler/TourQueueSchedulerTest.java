package scheduler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import server.consumer.ConsumerApplication;
import server.consumer.processor.TourQueueProcessor;
import server.consumer.scheduler.TourQueueScheduler;

import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

@EnableScheduling
@SpringBootTest(classes = ConsumerApplication.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TourQueueSchedulerTest {

	@Mock
	private TourQueueProcessor tourQueueProcessor;

	@InjectMocks
	private TourQueueScheduler tourQueueScheduler;

	private ThreadPoolTaskScheduler taskScheduler;

	@BeforeEach
	void setUp() {
		taskScheduler = new ThreadPoolTaskScheduler();
		taskScheduler.initialize();
	}

//	@Test
	void testConsumeMessagesWithScheduling() throws InterruptedException {
		// 테스트 환경의 스케줄러 초기화
		ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
		taskScheduler.initialize();

		// Runnable로 스케줄 작업 등록
		Runnable task = tourQueueScheduler::consumeMessages;
		taskScheduler.scheduleWithFixedDelay(task, 3000);

		// 일정 시간 동안 대기하여 작업 실행 확인
		Thread.sleep(5000);

		// `processMessage`가 호출되었는지 검증
		verify(tourQueueProcessor, atLeastOnce()).processMessage("tourRequest");

		// 스케줄러 종료
		taskScheduler.shutdown();
	}
}