package finpay.payment.shared.infrastructure.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.util.ReflectionTestUtils;

import finpay.payment.shared.infrastructure.security.JwtTokenService.Token;

class JwtTokenServiceTests {

	@Test
	void generatesHs256TokenWithIdentityClaimsAndExpiration() {
		SecurityConfig securityConfig = new SecurityConfig();
		SecretKey key = securityConfig.jwtSecretKey("test-secret-that-is-at-least-32-bytes-long");
		JwtTokenService tokenService = new JwtTokenService(securityConfig.jwtEncoder(key));
		ReflectionTestUtils.setField(tokenService, "expirationSeconds", 3600L);
		UUID userId = UUID.randomUUID();
		Instant beforeGeneration = Instant.now();

		Token token = tokenService.generate(userId, "Alice");
		JwtDecoder decoder = securityConfig.jwtDecoder(key);
		Jwt jwt = decoder.decode(token.getValue());

		assertEquals(userId.toString(), jwt.getSubject());
		assertEquals("Alice", jwt.getClaimAsString("username"));
		assertEquals("HS256", jwt.getHeaders().get("alg"));
		assertEquals(token.getExpiresAt(), jwt.getExpiresAt());
		assertTrue(token.getExpiresAt().isAfter(beforeGeneration.plusSeconds(3590)));
	}
}
