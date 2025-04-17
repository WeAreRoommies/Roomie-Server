package server.producer.security.jwt;

import entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final Key key;
    private final long accessTokenValidity;
    private final long refreshTokenValidity;

    public JwtTokenProvider(JwtProperties jwtProperties) {
        String base64Encoded = Base64.getEncoder().encodeToString(jwtProperties.getSecretKey().getBytes());
        this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(base64Encoded));
        this.accessTokenValidity = jwtProperties.getExpiration().getAccess();
        this.refreshTokenValidity = jwtProperties.getExpiration().getRefresh();
    }

    public String createToken(User user) {
        return createToken(user, accessTokenValidity);
    }

    public String createRefreshToken(User user) {
        return createToken(user, refreshTokenValidity);
    }

    public Long getUserId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("userId", Long.class);
    }

    private String createToken(User user, long validity) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("userId", user.getId())
                .claim("socialType", user.getSocialType().name())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(validity)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}

