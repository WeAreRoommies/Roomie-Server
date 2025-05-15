package server.producer.domain.service.strategy;

import entity.SocialType;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class KakaoLoginStrategy implements SocialLoginStrategy {

	private static final String KAKAO_USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";

	@Override
	public SocialType getSupportedType() {
		return SocialType.KAKAO;
	}

	@Override
	public SocialUserInfo getUserInfo(String accessToken) {
		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(accessToken);
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		HttpEntity<Void> request = new HttpEntity<>(headers);

		ResponseEntity<String> response = restTemplate.exchange(
				KAKAO_USER_INFO_URL,
				HttpMethod.GET,
				request,
				String.class
		);

		if (response.getStatusCode() != HttpStatus.OK) {
			throw new RuntimeException("카카오 사용자 정보 조회 실패");
		}

		JSONObject body = new JSONObject(response.getBody());
		JSONObject kakaoAccount = body.getJSONObject("kakao_account");
		JSONObject profile = kakaoAccount.getJSONObject("profile");

		String id = body.get("id").toString();
		String email = kakaoAccount.has("email") ? kakaoAccount.getString("email") : null;
		String nickname = profile.getString("nickname");
		String profileImage = profile.has("profile_image_url") ? profile.getString("profile_image_url") : null;

		return new SocialUserInfo(
				id,
				email,
				nickname,
				profileImage,
				SocialType.KAKAO
		);
	}
}