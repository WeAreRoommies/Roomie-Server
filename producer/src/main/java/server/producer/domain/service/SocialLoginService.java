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

	public SocialLoginResponseDto login(String provider, String accessToken) {
		SocialLoginStrategy strategy = getStrategy(provider);
		SocialUserInfo userInfo = strategy.getUserInfo(accessToken);

		User user = userRepository.findBySocialTypeAndSocialId(userInfo.getProvider(), userInfo.getId())
				.orElseThrow(() -> new RuntimeException("회원가입 필요"));

		String access = jwtProvider.createToken(user);
		String refresh = jwtProvider.createRefreshToken(user);
		refreshTokenRepository.save(refresh, user.getId());

		return SocialLoginResponseDto.builder()
				.accessToken(access)
				.refreshToken(refresh)
				.build();
	}

	public SocialSignupResponseDto signup(String provider, String accessToken, String nickname) {
		SocialLoginStrategy strategy = getStrategy(provider);
		SocialUserInfo userInfo = strategy.getUserInfo(accessToken);

		if (userRepository.findBySocialTypeAndSocialId(userInfo.getProvider(), userInfo.getId()).isPresent()) {
			throw new RuntimeException("이미 가입된 유저입니다");
		}

		User newUser = User.builder()
				.email(userInfo.getEmail())
				.nickname(nickname)
				.socialType(userInfo.getProvider())
				.socialId(userInfo.getId())
				.build();

		userRepository.save(newUser);

		String access = jwtProvider.createToken(newUser);
		String refresh = jwtProvider.createRefreshToken(newUser);
		refreshTokenRepository.save(refresh, newUser.getId());

		return SocialSignupResponseDto.builder()
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
