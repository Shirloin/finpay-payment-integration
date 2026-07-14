package finpay.payment.auth.application;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import finpay.payment.auth.domain.dto.AuthResponseDTO;
import finpay.payment.shared.infrastructure.exception.IncorrectPasswordException;
import finpay.payment.shared.infrastructure.exception.InvalidCredentialsException;
import finpay.payment.shared.infrastructure.exception.UsernameNotFoundException;
import finpay.payment.shared.infrastructure.security.BCryptPasswordHasher;
import finpay.payment.shared.infrastructure.security.JwtTokenService;
import finpay.payment.shared.infrastructure.security.JwtTokenService.Token;
import finpay.payment.user.domain.User;
import finpay.payment.user.domain.dto.UserResponseDTO;
import finpay.payment.user.infrastructure.UserJpaRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserJpaRepository userRepository;
	private final BCryptPasswordHasher passwordHasher;
	private final JwtTokenService tokenService;

	@Transactional(readOnly = true)
	public AuthResponseDTO login(String username, String password) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(UsernameNotFoundException::new);
		if (!passwordHasher.matches(password, user.getPassword())) {
			throw new IncorrectPasswordException();
		}
		Token token = tokenService.generate(user.getId(), user.getUsername());
		return AuthResponseDTO.builder()
				.accessToken(token.getValue())
				.tokenType("Bearer")
				.expiresAt(token.getExpiresAt())
				.user(UserResponseDTO.from(user))
				.build();
	}

	@Transactional(readOnly = true)
	public UserResponseDTO verify(String subject) {
		UUID userId;
		try {
			userId = UUID.fromString(subject);
		} catch (IllegalArgumentException exception) {
			throw new InvalidCredentialsException();
		}
		User user = userRepository.findById(userId)
				.orElseThrow(InvalidCredentialsException::new);
		return UserResponseDTO.from(user);
	}
}
