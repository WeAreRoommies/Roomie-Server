package server.producer.domain.dto.request;

import lombok.Getter;

@Getter
public class SocialLoginRequestDto {
		private String provider;
		private String accessToken;
}