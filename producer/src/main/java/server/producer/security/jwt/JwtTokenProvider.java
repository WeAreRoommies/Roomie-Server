package server.producer.security.jwt;

import entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final Key key;
    private final long accessTokenValidity;

    public JwtTokenProvider(@Value("${jwt.secret-key}") String secretKey,
                            @Value("${jwt.expiration.access}") long accessTokenValidity) {
        String base64Encoded = Base64.getEncoder().encodeToString(secretKey.getBytes());
        this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(base64Encoded));
        this.accessTokenValidity = accessTokenValidity;
    }

    // 사용자 정보 기반 JWT 토큰 생성
    public String createToken(User user) {
        Instant now = Instant.now();

        return Jwts.builder()
                .setSubject(user.getEmail()) // 이메일을 subject로 설정
                .claim("userId", user.getId()) // 사용자 ID 포함 (선택)
                .claim("socialType", user.getSocialType().name()) // 소셜 타입 포함 (선택)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(accessTokenValidity)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}