package server.producer.domain.service.strategy;

import entity.SocialType;
import org.springframework.stereotype.Component;

// 애플 로그인 전략
@Component
public class AppleLoginStrategy implements SocialLoginStrategy {
	@Override
	public SocialType getSupportedType() {
		return SocialType.APPLE;
	}

	@Override
	public SocialUserInfo getUserInfo(String idToken) {
		// 애플 id_token 디코딩 및 검증 로직 생략
		return new SocialUserInfo("apple-id", "email@apple.com", "애플유저", null, SocialType.APPLE);
	}
}