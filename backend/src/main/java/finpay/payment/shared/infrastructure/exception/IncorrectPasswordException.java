package finpay.payment.shared.infrastructure.exception;

public class IncorrectPasswordException extends RuntimeException {

	public IncorrectPasswordException() {
		super("Incorrect password");
	}
}
