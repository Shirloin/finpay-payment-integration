package finpay.payment.shared.infrastructure.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BCryptPasswordHasher {

	private final PasswordEncoder passwordEncoder;

	public String hash(String rawPassword) {
		return passwordEncoder.encode(rawPassword);
	}
}
