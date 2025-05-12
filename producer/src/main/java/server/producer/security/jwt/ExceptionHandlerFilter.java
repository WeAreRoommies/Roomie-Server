package server.producer.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import server.producer.domain.repository.UserRepository;
import server.producer.security.jwt.JwtTokenProvider;
import server.producer.security.jwt.RefreshTokenRepository;
import entity.User;

import java.io.IOException;

public class ExceptionHandlerFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public ExceptionHandlerFilter(JwtTokenProvider jwtTokenProvider,
                                  RefreshTokenRepository refreshTokenRepository,
                                  UserRepository userRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            handleExpiredAccessToken(request, response);
        }
    }

    private void handleExpiredAccessToken(HttpServletRequest request,
                                          HttpServletResponse response) throws IOException {

        String refreshToken = request.getHeader("Refresh-Token");
        if (refreshToken == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "AccessToken 만료 + RefreshToken 없음");
            return;
        }

        Long userId = refreshTokenRepository.findUserIdByToken(refreshToken)
                .orElse(null);

        if (userId == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 RefreshToken");
            return;
        }

        User user = userRepository.findById(userId)
                .orElse(null);

        if (user == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유저 없음");
            return;
        }

        String newAccessToken = jwtTokenProvider.createToken(user);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 또는 200으로 응답하고 클라이언트에게 알림
        response.setHeader("New-Access-Token", newAccessToken);
        response.getWriter().write("AccessToken이 만료되어 새 토큰을 발급했습니다.");
    }
}

