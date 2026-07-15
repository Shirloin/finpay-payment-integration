package finpay.payment.order.domain.dto;

import java.time.Instant;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TopUpResponseDTO {

	private final String orderId;
	private final long amount;
	private final String paymentCode;
	private final String redirectUrl;
	private final String expiryLink;
	private final String status;
	private final Instant createdAt;
}
