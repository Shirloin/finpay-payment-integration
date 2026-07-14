package finpay.payment.shared.infrastructure.exception;

public class InvalidCredentialsException extends RuntimeException {

	public InvalidCredentialsException() {
		super("Invalid username or password");
	}
}
