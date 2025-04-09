package server.producer.security.jwt;

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

    // 토큰 생성 (이메일, 역할, 유효기간 전달)
    public String createToken(String email, String role) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(accessTokenValidity)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
