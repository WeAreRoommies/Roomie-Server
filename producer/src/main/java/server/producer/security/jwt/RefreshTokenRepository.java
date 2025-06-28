package server.producer.security.jwt;

import java.util.Optional;

public interface RefreshTokenRepository {
    void save(String token, Long userId);
    Optional<Long> findUserIdByToken(String token);
    void delete(String token);
    void deleteByToken(String token);
}

