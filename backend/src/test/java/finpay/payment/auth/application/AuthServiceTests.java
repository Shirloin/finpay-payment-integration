package finpay.payment.auth.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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

@ExtendWith(MockitoExtension.class)
class AuthServiceTests {

	@Mock
	private UserJpaRepository repository;

	@Mock
	private BCryptPasswordHasher passwordHasher;

	@Mock
	private JwtTokenService tokenService;

	private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
	private AuthService service;

	@BeforeEach
	void setUp() {
		service = new AuthService(repository, passwordHasher, tokenService);
	}

	@Test
	void logsInUsingExactUsernameAndBcryptVerification() {
		User user = user("Alice");
		Instant expiresAt = Instant.now().plusSeconds(3600);
		when(repository.findByUsername("Alice")).thenReturn(Optional.of(user));
		when(passwordHasher.matches("secret1", user.getPassword())).thenReturn(true);
		when(tokenService.generate(user.getId(), "Alice")).thenReturn(new Token("jwt-token", expiresAt));

		AuthResponseDTO response = service.login("Alice", "secret1");

		assertEquals("jwt-token", response.getAccessToken());
		assertEquals("Bearer", response.getTokenType());
		assertEquals(expiresAt, response.getExpiresAt());
		assertEquals("Alice", response.getUser().getUsername());
		verify(repository).findByUsername("Alice");
	}

	@Test
	void rejectsMissingExactUsernameWithoutCheckingPassword() {
		when(repository.findByUsername("alice")).thenReturn(Optional.empty());

		assertThrows(UsernameNotFoundException.class, () -> service.login("alice", "secret1"));

		verify(passwordHasher, never()).matches(any(), any());
		verify(tokenService, never()).generate(any(), any());
	}

	@Test
	void rejectsIncorrectPasswordWithoutIssuingToken() {
		User user = user("Alice");
		when(repository.findByUsername("Alice")).thenReturn(Optional.of(user));
		when(passwordHasher.matches("wrong-password", user.getPassword())).thenReturn(false);

		assertThrows(IncorrectPasswordException.class, () -> service.login("Alice", "wrong-password"));

		verify(tokenService, never()).generate(any(), any());
	}

	@Test
	void verifiesCurrentUserByUuidSubject() {
		User user = user("Alice");
		when(repository.findById(user.getId())).thenReturn(Optional.of(user));

		UserResponseDTO response = service.verify(user.getId().toString());

		assertEquals(user.getId(), response.getId());
		assertEquals("Alice", response.getUsername());
		assertEquals(user.getCreatedAt(), response.getCreatedAt());
		verify(repository).findById(user.getId());
	}

	@Test
	void rejectsTokenWhenSubjectUserNoLongerExists() {
		UUID userId = UUID.randomUUID();
		when(repository.findById(userId)).thenReturn(Optional.empty());

		assertThrows(InvalidCredentialsException.class, () -> service.verify(userId.toString()));
	}

	private User user(String username) {
		return User.builder()
				.id(UUID.randomUUID())
				.username(username)
				.password(encoder.encode("secret1"))
				.createdAt(Instant.now())
				.build();
	}
}
