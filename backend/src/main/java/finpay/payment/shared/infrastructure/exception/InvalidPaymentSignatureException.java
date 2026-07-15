package finpay.payment.shared.infrastructure.exception;

public class InvalidPaymentSignatureException extends RuntimeException {

	public InvalidPaymentSignatureException() {
		super("Invalid payment callback signature");
	}
}
