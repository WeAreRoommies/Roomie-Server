package server.producer.security.jwt;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt")
@Getter
public class JwtProperties {
    private String secretKey;
    private Expiration expiration;

    @Getter
    public static class Expiration {
        private long access;
        private long refresh;
    }
}

