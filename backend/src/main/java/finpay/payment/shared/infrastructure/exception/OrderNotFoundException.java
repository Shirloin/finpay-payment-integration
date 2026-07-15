package finpay.payment.shared.infrastructure.exception;

public class OrderNotFoundException extends RuntimeException {

	public OrderNotFoundException(String orderId) {
		super("Order '" + orderId + "' was not found");
	}
}
