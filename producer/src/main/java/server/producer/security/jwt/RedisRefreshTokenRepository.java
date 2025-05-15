package server.producer.security.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RedisRefreshTokenRepository implements RefreshTokenRepository {

    private final StringRedisTemplate redisTemplate;

    private static final long REFRESH_TOKEN_EXPIRATION_MS = 604800000; // 7일

    @Override
    public void save(String token, Long userId) {
        redisTemplate.opsForValue().set(token, userId.toString(), REFRESH_TOKEN_EXPIRATION_MS, TimeUnit.MILLISECONDS);
    }

    @Override
    public Optional<Long> findUserIdByToken(String token) {
        String userId = redisTemplate.opsForValue().get(token);
        return Optional.ofNullable(userId).map(Long::valueOf);
    }

    @Override
    public void delete(String token) {
        redisTemplate.delete(token);
    }
}

