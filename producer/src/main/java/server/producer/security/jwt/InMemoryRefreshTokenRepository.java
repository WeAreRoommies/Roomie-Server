package server.producer.security.jwt;

import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

// 나중에 redis로 교체 가능
@Repository
public class InMemoryRefreshTokenRepository implements RefreshTokenRepository {

    private final Map<String, Long> store = new ConcurrentHashMap<>();

    @Override
    public void save(String token, Long userId) {
        store.put(token, userId);
    }

    @Override
    public Optional<Long> findUserIdByToken(String token) {
        return Optional.ofNullable(store.get(token));
    }

    @Override
    public void delete(String token) {
        store.remove(token);
    }
}

