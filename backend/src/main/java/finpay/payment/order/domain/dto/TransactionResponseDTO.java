package finpay.payment.order.domain.dto;

import java.time.Instant;
import java.util.UUID;

import finpay.payment.order.domain.Order;
import finpay.payment.order.domain.OrderStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TransactionResponseDTO {

	private final UUID id;
	private final String orderId;
	private final long amount;
	private final OrderStatus status;
	private final String paymentMethod;
	private final String paymentCode;
	private final String redirectUrl;
	private final Instant createdAt;

	public static TransactionResponseDTO from(Order order) {
		return TransactionResponseDTO.builder()
				.id(order.getId())
				.orderId(order.getOrderId())
				.amount(order.getAmount())
				.status(order.getStatus())
				.paymentMethod(order.getPaymentMethod())
				.paymentCode(order.getPaymentCode())
				.redirectUrl(order.getRedirectUrl())
				.createdAt(order.getCreatedAt())
				.build();
	}
}
