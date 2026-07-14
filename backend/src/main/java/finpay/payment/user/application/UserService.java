package finpay.payment.user.application;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import finpay.payment.shared.infrastructure.exception.DuplicateUsernameException;
import finpay.payment.shared.infrastructure.security.BCryptPasswordHasher;
import finpay.payment.user.domain.User;
import finpay.payment.user.domain.dto.UserResponseDTO;
import finpay.payment.user.infrastructure.UserJpaRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserJpaRepository userRepository;
	private final BCryptPasswordHasher passwordHasher;

	@Transactional
	public UserResponseDTO register(String username, String password) {
		if (userRepository.existsByUsername(username)) {
			throw new DuplicateUsernameException(username);
		}

		User user = User.builder()
				.id(UUID.randomUUID())
				.username(username)
				.password(passwordHasher.hash(password))
				.build();
		return UserResponseDTO.from(userRepository.save(user));
	}
}
