package finpay.payment.shared.infrastructure.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtTokenService {

	private final JwtEncoder jwtEncoder;

	@Value("${jwt.expiration-seconds}")
	private long expirationSeconds;

	public Token generate(UUID userId, String username) {
		Instant issuedAt = Instant.now().truncatedTo(ChronoUnit.SECONDS);
		Instant expiresAt = issuedAt.plus(expirationSeconds, ChronoUnit.SECONDS);
		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuedAt(issuedAt)
				.expiresAt(expiresAt)
				.subject(userId.toString())
				.claim("username", username)
				.build();
		JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
		String value = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
		return new Token(value, expiresAt);
	}

	@Getter
	@RequiredArgsConstructor
	public static class Token {

		private final String value;
		private final Instant expiresAt;
	}
}
