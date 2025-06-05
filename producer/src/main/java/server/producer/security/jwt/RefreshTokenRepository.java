package server.producer.security.jwt;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository {
    void save(String token, Long userId);
    Optional<Long> findUserIdByToken(String token);
    void delete(String token);
    void deleteByToken(String token);
}

