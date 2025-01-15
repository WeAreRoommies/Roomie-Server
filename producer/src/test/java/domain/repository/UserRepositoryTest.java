package domain.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import server.producer.ProducerApplication;
import server.producer.domain.repository.UserRepository;
import server.producer.entity.User;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ContextConfiguration(classes = ProducerApplication.class)
public class UserRepositoryTest {

	@Autowired
	private UserRepository userRepository;

	@BeforeEach
	void setUp() {
		// 테스트 데이터 삽입
		User user1 = new User();
		user1.setName("John Doe");
		user1.setLocation("서대문구 대현동");
		userRepository.save(user1);

		User user2 = new User();
		user2.setName("Jane Doe");
		user2.setLocation("강남구 역삼동");
		userRepository.save(user2);
	}

	@Test
	void testFindLocationById_ValidUserId() {
		// given
		Long userId = 1L;

		// when
		Optional<String> location = userRepository.findLocationById(userId);

		// then
		assertTrue(location.isPresent());
		assertEquals("서대문구 대현동", location.get());
	}

	@Test
	void testFindLocationById_InvalidUserId() {
		// given
		Long userId = 99L;

		// when
		Optional<String> location = userRepository.findLocationById(userId);

		// then
		assertTrue(location.isEmpty());
	}
}