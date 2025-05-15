package server.producer.domain.service.strategy;

import entity.SocialType;

public interface SocialLoginStrategy {
	SocialType getSupportedType();
	SocialUserInfo getUserInfo(String accessToken);
}