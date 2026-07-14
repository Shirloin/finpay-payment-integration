package finpay.payment.user.infrastructure;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import finpay.payment.user.domain.User;

public interface UserJpaRepository extends JpaRepository<User, UUID> {

	boolean existsByUsername(String username);
}
