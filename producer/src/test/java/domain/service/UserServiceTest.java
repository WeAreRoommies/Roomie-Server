package domain.service;

import entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import server.producer.domain.dto.response.MyPageResponseDto;
import server.producer.domain.repository.UserRepository;
import server.producer.domain.service.UserService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @InjectMocks
    private UserService userService;

    @Test
    void testGetMyPage() {
        // Given: Mock 데이터 설정
        Long userId = 1L;
        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setName("홍길동");

        // userRepository의 동작 Mock 설정
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        // When: 서비스 호출
        MyPageResponseDto response = userService.getMyPage(userId);

        // Then: 변환 결과 검증
        assertNotNull(response); // 응답이 null이 아님
        assertEquals("홍길동", response.name()); // 이름 필드가 기대값과 일치
    }

}
