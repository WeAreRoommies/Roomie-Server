package server.producer.domain.service;

import entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import server.producer.domain.repository.UserRepository;
import server.producer.domain.service.strategy.SocialLoginStrategy;
import server.producer.domain.service.strategy.SocialUserInfo;
import server.producer.security.jwt.JwtTokenProvider;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SocialLoginService {
	private final List<SocialLoginStrategy> strategies;
	private final UserRepository userRepository;
	private final JwtTokenProvider jwtProvider;

	public String login(String provider, String accessToken) {
		SocialLoginStrategy strategy = getStrategy(provider);
		SocialUserInfo userInfo = strategy.getUserInfo(accessToken);

		User user = userRepository.findBySocialTypeAndSocialId(userInfo.getProvider(), userInfo.getId())
				.orElseThrow(() -> new RuntimeException("회원가입 필요"));

		return jwtProvider.createToken(user);
	}

	public String signup(String provider, String accessToken, String nickname) {
		SocialLoginStrategy strategy = getStrategy(provider);
		SocialUserInfo userInfo = strategy.getUserInfo(accessToken);

		if (userRepository.findBySocialTypeAndSocialId(userInfo.getProvider(), userInfo.getId()).isPresent()) {
			throw new RuntimeException("이미 가입된 유저입니다");
		}

		User newUser = User.builder()
				.name(userInfo.getNickname())
				.location("마포구 노고산동")
				.socialType(userInfo.getProvider())
				.socialId(userInfo.getId())
				.build();

		userRepository.save(newUser);
		return jwtProvider.createToken(newUser);
	}

	private SocialLoginStrategy getStrategy(String provider) {
		return strategies.stream()
				.filter(s -> s.getSupportedType().name().equalsIgnoreCase(provider))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("지원하지 않는 provider"));
	}

}
