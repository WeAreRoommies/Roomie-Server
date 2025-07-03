package server.producer.domain.service;

import entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import server.producer.domain.dto.response.SocialLoginResponseDto;
import server.producer.domain.dto.response.SocialSignupResponseDto;
import server.producer.domain.repository.UserRepository;
import server.producer.domain.service.strategy.SocialLoginStrategy;
import server.producer.domain.service.strategy.SocialUserInfo;
import server.producer.security.jwt.JwtTokenProvider;
import server.producer.security.jwt.RefreshTokenRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SocialLoginService {
	private final List<SocialLoginStrategy> strategies;
	private final UserRepository userRepository;
	private final JwtTokenProvider jwtProvider;
	private final RefreshTokenRepository refreshTokenRepository;

	public SocialLoginResponseDto loginOrSignup(String provider, String accessToken) {
		SocialLoginStrategy strategy = getStrategy(provider);
		SocialUserInfo userInfo = strategy.getUserInfo(accessToken);

		User user = userRepository.findBySocialTypeAndSocialId(userInfo.getProvider(), userInfo.getId())
				.orElseGet(() -> {
					User created = User.builder()
							.socialId(userInfo.getId())
							.socialType(userInfo.getProvider())
							.email(userInfo.getEmail())
							.nickname(userInfo.getNickname())
							.location("서울특별시 서대문구 창천동")
							.build();
					return userRepository.save(created);
				});

		String access = jwtProvider.createToken(user);
		String refresh = jwtProvider.createRefreshToken(user);
		refreshTokenRepository.save(refresh, user.getId());

		return SocialLoginResponseDto.builder()
				.accessToken(access)
				.refreshToken(refresh)
				.build();
	}

	private SocialLoginStrategy getStrategy(String provider) {
		return strategies.stream()
				.filter(s -> s.getSupportedType().name().equalsIgnoreCase(provider))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("지원하지 않는 provider"));
	}
}
