package server.producer.security.jwt;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {}

