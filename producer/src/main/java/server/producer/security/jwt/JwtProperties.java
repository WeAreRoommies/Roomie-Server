package server.producer.security.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secret;
    private Expiration expiration;

    @Getter
    @Setter
    public static class Expiration {
        private long access;  // access token 만료 시간
    }
}

