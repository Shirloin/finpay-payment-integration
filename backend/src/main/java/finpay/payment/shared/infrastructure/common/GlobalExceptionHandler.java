package finpay.payment.shared.infrastructure.common;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import finpay.payment.shared.infrastructure.exception.DuplicateUsernameException;
import finpay.payment.shared.infrastructure.exception.FinpayApiException;
import finpay.payment.shared.infrastructure.exception.IncorrectPasswordException;
import finpay.payment.shared.infrastructure.exception.InvalidCredentialsException;
import finpay.payment.shared.infrastructure.exception.InvalidPaymentSignatureException;
import finpay.payment.shared.infrastructure.exception.OrderNotFoundException;
import finpay.payment.shared.infrastructure.exception.UsernameNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(DuplicateUsernameException.class)
	public ResponseEntity<ApiResponse<Void>> handleDuplicateUsername(
			DuplicateUsernameException exception,
			HttpServletRequest request) {
		return error(HttpStatus.CONFLICT, exception.getMessage(), request.getRequestURI(), Map.of());
	}

	@ExceptionHandler({
			InvalidCredentialsException.class,
			UsernameNotFoundException.class,
			IncorrectPasswordException.class
	})
	public ResponseEntity<ApiResponse<Void>> handleAuthenticationFailure(
			RuntimeException exception,
			HttpServletRequest request) {
		return error(HttpStatus.UNAUTHORIZED, exception.getMessage(), request.getRequestURI(), Map.of());
	}

	@ExceptionHandler(OrderNotFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handleOrderNotFound(
			OrderNotFoundException exception,
			HttpServletRequest request) {
		return error(HttpStatus.NOT_FOUND, exception.getMessage(), request.getRequestURI(), Map.of());
	}

	@ExceptionHandler(InvalidPaymentSignatureException.class)
	public ResponseEntity<ApiResponse<Void>> handleInvalidPaymentSignature(
			InvalidPaymentSignatureException exception,
			HttpServletRequest request) {
		return error(HttpStatus.UNAUTHORIZED, exception.getMessage(), request.getRequestURI(), Map.of());
	}

	@ExceptionHandler(FinpayApiException.class)
	public ResponseEntity<ApiResponse<Void>> handleFinpayApiException(
			FinpayApiException exception,
			HttpServletRequest request) {
		return error(HttpStatus.BAD_GATEWAY, exception.getMessage(), request.getRequestURI(), Map.of());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Void>> handleValidation(
			MethodArgumentNotValidException exception,
			HttpServletRequest request) {
		Map<String, String> fieldErrors = new LinkedHashMap<>();
		exception.getBindingResult().getFieldErrors().forEach(fieldError ->
				fieldErrors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage()));
		return error(HttpStatus.BAD_REQUEST, "Request validation failed", request.getRequestURI(), fieldErrors);
	}

	private ResponseEntity<ApiResponse<Void>> error(
			HttpStatus status,
			String message,
			String path,
			Map<String, String> fieldErrors) {
		ApiError body = new ApiError(
				Instant.now(),
				status.value(),
				status.getReasonPhrase(),
				message,
				path,
				fieldErrors);
		return ResponseEntity.status(status).body(ApiResponse.failure(message, body));
	}
}
