package server.producer.domain.service.strategy;

import entity.SocialType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SocialUserInfo {
	private final String id;
	private final String email;
	private final String nickname;
	private final String profileImage;
	private final SocialType provider;
}
