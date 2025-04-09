package server.producer.domain.dto.request;

import lombok.Getter;

@Getter
public class SocialSignupRequestDto {
		private String provider;
		private String accessToken;
		private String nickname;
}

