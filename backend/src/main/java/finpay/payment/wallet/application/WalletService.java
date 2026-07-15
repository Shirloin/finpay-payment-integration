package finpay.payment.wallet.application;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import finpay.payment.user.domain.User;
import finpay.payment.wallet.domain.Wallet;
import finpay.payment.wallet.infrastructure.WalletJpaRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WalletService {

	private final WalletJpaRepository walletRepository;

	@Transactional(readOnly = true)
	public long getBalance(UUID userId) {
		return walletRepository.findByUser_Id(userId)
				.map(Wallet::getBalance)
				.orElse(0L);
	}

	@Transactional
	public Wallet getOrCreate(User user) {
		return walletRepository.findByUser_Id(user.getId())
				.orElseGet(() -> walletRepository.save(Wallet.builder()
						.id(UUID.randomUUID())
						.user(user)
						.balance(0L)
						.build()));
	}

	@Transactional
	public long credit(User user, long amount) {
		Wallet wallet = getOrCreate(user);
		wallet.credit(amount);
		return wallet.getBalance();
	}
}
