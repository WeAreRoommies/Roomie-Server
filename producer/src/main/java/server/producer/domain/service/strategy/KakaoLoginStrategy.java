package server.producer.domain.service.strategy;

import entity.SocialType;
import org.springframework.stereotype.Component;

// 카카오 로그인 전략
@Component
public class KakaoLoginStrategy implements SocialLoginStrategy {
	@Override
	public SocialType getSupportedType() {
		return SocialType.KAKAO;
	}

	@Override
	public SocialUserInfo getUserInfo(String accessToken) {
		// 카카오 API 호출 로직 생략
		return new SocialUserInfo("kakao-id", "email@kakao.com", "카카오유저", null, SocialType.KAKAO);
	}
}