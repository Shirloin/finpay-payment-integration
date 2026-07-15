package finpay.payment.shared.infrastructure.exception;

import finpay.payment.finpay.dto.FinpayErrorResponseDTO;
import tools.jackson.databind.json.JsonMapper;

public class FinpayApiException extends RuntimeException {

	private static final JsonMapper JSON_MAPPER = JsonMapper.builder().build();

	public FinpayApiException(String message) {
		super(message);
	}

	public static FinpayApiException fromResponseBody(String responseBody) {
		try {
			FinpayErrorResponseDTO error = JSON_MAPPER.readValue(responseBody, FinpayErrorResponseDTO.class);
			if (error.getResponseCode() != null) {
				return new FinpayApiException(formatError(error));
			}
		} catch (Exception ignored) {
			// Fall back to raw body below.
		}
		return new FinpayApiException("Finpay API request failed: " + responseBody);
	}

	private static String formatError(FinpayErrorResponseDTO error) {
		if ("4030001".equals(error.getResponseCode())) {
			return "Finpay rejected the request (4030001 Feature Not Allowed). "
					+ "Contact Finpay to confirm your merchant account has the required payment features enabled.";
		}
		return "Finpay API request failed: " + error.getResponseCode() + " - " + error.getResponseMessage();
	}
}
