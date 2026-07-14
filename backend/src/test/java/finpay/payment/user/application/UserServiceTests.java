package finpay.payment.user.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import finpay.payment.shared.infrastructure.exception.DuplicateUsernameException;
import finpay.payment.shared.infrastructure.security.BCryptPasswordHasher;
import finpay.payment.user.domain.User;
import finpay.payment.user.domain.dto.UserResponseDTO;
import finpay.payment.user.infrastructure.UserJpaRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {

	@Mock
	private UserJpaRepository repository;

	@Mock
	private BCryptPasswordHasher passwordHasher;

	private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
	private UserService service;

	@BeforeEach
	void setUp() {
		service = new UserService(repository, passwordHasher);
	}

	@Test
	void registersUserWithBcryptPasswordHash() {
		String hash = encoder.encode("secret1");
		when(passwordHasher.hash("secret1")).thenReturn(hash);
		when(repository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

		UserResponseDTO response = service.register("Alice", "secret1");
		ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
		verify(repository).save(userCaptor.capture());
		User savedUser = userCaptor.getValue();

		assertEquals("Alice", response.getUsername());
		assertNotEquals("secret1", savedUser.getPassword());
		assertTrue(encoder.matches("secret1", savedUser.getPassword()));
	}

	@Test
	void treatsUsernameCaseAsProvided() {
		when(passwordHasher.hash(any())).thenReturn("bcrypt-hash");
		when(repository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
		service.register("Alice", "secret1");
		UserResponseDTO secondUser = service.register("alice", "secret2");

		assertEquals("alice", secondUser.getUsername());
		verify(repository, times(2)).save(any(User.class));
	}

	@Test
	void preservesUsernameWhitespace() {
		when(passwordHasher.hash("secret1")).thenReturn("bcrypt-hash");
		when(repository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
		UserResponseDTO user = service.register(" Alice ", "secret1");

		assertEquals(" Alice ", user.getUsername());
	}

	@Test
	void rejectsAnExactDuplicateUsername() {
		when(repository.existsByUsername("Alice")).thenReturn(true);

		assertThrows(DuplicateUsernameException.class,
				() -> service.register("Alice", "secret2"));
		verify(passwordHasher, never()).hash(any());
		verify(repository, never()).save(any());
	}

	@Test
	void checksForDuplicateBeforeSaving() {
		when(passwordHasher.hash("secret1")).thenReturn("bcrypt-hash");
		when(repository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

		service.register("Alice", "secret1");

		ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
		InOrder order = inOrder(repository);
		order.verify(repository).existsByUsername("Alice");
		order.verify(repository).save(userCaptor.capture());
		assertEquals("Alice", userCaptor.getValue().getUsername());
	}
}
