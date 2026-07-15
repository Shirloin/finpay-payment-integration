package finpay.payment.finpay.infrastructure;

import org.springframework.stereotype.Component;

import finpay.payment.finpay.dto.FinpayCustomerDTO;
import finpay.payment.finpay.dto.FinpayInitiateRequestDTO;
import finpay.payment.finpay.dto.FinpayOrderDTO;
import finpay.payment.finpay.dto.FinpayUrlDTO;
import finpay.payment.user.domain.User;

@Component
public class FinpayRequestFactory {

	public String generateOrderId() {
		long timestamp = System.currentTimeMillis();
		int suffix = java.util.concurrent.ThreadLocalRandom.current().nextInt(100, 999);
		return timestamp + String.valueOf(suffix);
	}

	public FinpayInitiateRequestDTO buildInitiateRequest(
			User user,
			String orderId,
			long amount,
			FinpayProperties properties) {
		String customerId = user.getUsername();
		String email = customerId + "@payment.com";

		return FinpayInitiateRequestDTO.builder()
				.order(FinpayOrderDTO.builder()
						.id(orderId)
						.amount(String.valueOf(amount))
						.currency("IDR")
						.description("Wallet top up")
						.build())
				.customer(FinpayCustomerDTO.builder()
						.id(customerId)
						.email(email)
						.firstName(customerId)
						.lastName(customerId)
						.mobilePhone(randomMobilePhone())
						.build())
				.url(FinpayUrlDTO.builder()
						.successUrl(properties.getSuccessUrl() + "?orderId=" + orderId)
						.failUrl(properties.getFailUrl() + "?orderId=" + orderId)
						.callbackUrl(properties.getCallbackUrl())
						.build())
				.build();
	}

	private String randomMobilePhone() {
		long number = java.util.concurrent.ThreadLocalRandom.current().nextLong(10_000_000L, 99_999_999L);
		return "+6281" + number;
	}
}
