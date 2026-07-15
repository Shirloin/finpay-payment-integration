package finpay.payment.order.domain.dto;

import finpay.payment.order.domain.Order;
import finpay.payment.order.domain.OrderStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderStatusResponseDTO {

	private final String orderId;
	private final long amount;
	private final OrderStatus status;
	private final String paymentMethod;
	private final String paymentCode;
	private final String redirectUrl;
	private final long balance;

	public static OrderStatusResponseDTO of(Order order, long balance) {
		return OrderStatusResponseDTO.builder()
				.orderId(order.getOrderId())
				.amount(order.getAmount())
				.status(order.getStatus())
				.paymentMethod(order.getPaymentMethod())
				.paymentCode(order.getPaymentCode())
				.redirectUrl(order.getRedirectUrl())
				.balance(balance)
				.build();
	}
}
