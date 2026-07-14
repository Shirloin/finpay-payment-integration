package finpay.payment.shared.infrastructure.exception;

public class UsernameNotFoundException extends RuntimeException {

	public UsernameNotFoundException() {
		super("Username not found");
	}
}
