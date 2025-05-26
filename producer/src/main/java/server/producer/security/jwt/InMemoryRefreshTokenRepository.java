package server.producer.security.jwt;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryRefreshTokenRepository implements RefreshTokenRepository {

    private final Map<String, Long> tokenToUserId = new ConcurrentHashMap<>();

    @Override
    public void save(String token, Long userId) {
        tokenToUserId.put(token, userId);
    }

    @Override
    public Optional<Long> findUserIdByToken(String token) {
        return Optional.ofNullable(tokenToUserId.get(token));
    }

    @Override
    public void delete(String token) {
        tokenToUserId.remove(token);
    }
}

