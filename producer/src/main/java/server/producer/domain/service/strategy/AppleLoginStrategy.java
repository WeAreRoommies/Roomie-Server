package server.producer.domain.service.strategy;

import entity.SocialType;
import org.springframework.stereotype.Component;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jose.crypto.RSASSAVerifier;

import java.net.URL;
import java.util.List;

@Component
public class AppleLoginStrategy implements SocialLoginStrategy {

	private static final String APPLE_PUBLIC_KEYS_URL = "https://appleid.apple.com/auth/keys";

	@Override
	public SocialType getSupportedType() {
		return SocialType.APPLE;
	}

	@Override
	public SocialUserInfo getUserInfo(String idToken) {
		try {
			// JWT 파싱
			SignedJWT signedJWT = SignedJWT.parse(idToken);
			JWSHeader header = signedJWT.getHeader();
			String keyId = header.getKeyID();
			JWSAlgorithm algorithm = header.getAlgorithm();

			// 공개키 가져오기
			JWKSet publicKeys = JWKSet.load(new URL(APPLE_PUBLIC_KEYS_URL));
			List<JWK> keys = publicKeys.getKeys();

			// 공개키 중 일치하는 키 찾기
			RSAKey rsaKey = keys.stream()
					.filter(key -> key.getKeyID().equals(keyId))
					.map(key -> (RSAKey) key)
					.findFirst()
					.orElseThrow(() -> new RuntimeException("Apple 공개키를 찾을 수 없습니다."));

			// 검증자 생성
			RSASSAVerifier verifier = new RSASSAVerifier(rsaKey);

			// 서명 검증
			if (!signedJWT.verify(verifier)) {
				throw new RuntimeException("Apple id_token 서명 검증 실패");
			}

			// 페이로드에서 사용자 정보 추출
			String sub = signedJWT.getJWTClaimsSet().getSubject(); // 고유 ID
			String email = signedJWT.getJWTClaimsSet().getStringClaim("email");

			return new SocialUserInfo(
					sub,
					email,
					"애플유저",
					null,
					SocialType.APPLE
			);

		} catch (Exception e) {
			throw new RuntimeException("Apple id_token 처리 중 오류", e);
		}
	}
}