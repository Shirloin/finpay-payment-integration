package finpay.payment.wallet.infrastructure;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import finpay.payment.wallet.domain.Wallet;

public interface WalletJpaRepository extends JpaRepository<Wallet, UUID> {

	Optional<Wallet> findByUser_Id(UUID userId);
}
