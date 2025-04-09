package server.producer.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import server.producer.domain.repository.UserRepository;
import server.producer.domain.service.strategy.SocialLoginStrategy;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SocialLoginService {
	private final List<SocialLoginStrategy> strategies;
	private final UserRepository userRepository;
	private final JwtProvider jwtProvider;

	public String login(String provider, String accessToken) {
		SocialLoginStrategy strategy = getStrategy(provider);
		SocialUserInfo userInfo = strategy.getUserInfo(accessToken);

		User user = userRepository.findBySocialTypeAndSocialId(userInfo.getProvider(), userInfo.getId())
				.orElseThrow(() -> new RuntimeException("회원가입 필요"));

		return jwtProvider.createAccessToken(user);
	}

	public String signup(String provider, String accessToken, String nickname) {
		SocialLoginStrategy strategy = getStrategy(provider);
		SocialUserInfo userInfo = strategy.getUserInfo(accessToken);

		if (userRepository.findBySocialTypeAndSocialId(userInfo.getProvider(), userInfo.getId()).isPresent()) {
			throw new RuntimeException("이미 가입된 유저입니다");
		}

		User newUser = new User(/*생성자 인자 생략*/);
		// 필드 설정 생략

		userRepository.save(newUser);
		return jwtProvider.createAccessToken(newUser);
	}

	private SocialLoginStrategy getStrategy(String provider) {
		return strategies.stream()
				.filter(s -> s.getSupportedType().name().equalsIgnoreCase(provider))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("지원하지 않는 provider"));
	}

}
