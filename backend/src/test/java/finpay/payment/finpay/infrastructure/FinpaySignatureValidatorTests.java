package finpay.payment.finpay.infrastructure;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import finpay.payment.shared.infrastructure.exception.InvalidPaymentSignatureException;
import finpay.payment.finpay.dto.FinpayCallbackCustomerDTO;
import finpay.payment.finpay.dto.FinpayCallbackOrderDTO;
import finpay.payment.finpay.dto.FinpayCallbackSignaturePayloadDTO;

class FinpaySignatureValidatorTests {

	private static final FinpaySignatureValidator VALIDATOR = new FinpaySignatureValidator(
			tools.jackson.databind.json.JsonMapper.builder().build());

	@Test
	void acceptsSignatureComputedFromJsonWithoutSignatureField() throws Exception {
		String rawJson = FinpayCallbackTestSupport.signedJson(samplePayload(), "merchant-key");

		assertDoesNotThrow(() -> VALIDATOR.validateAndParse(rawJson, "merchant-key"));
	}

	@Test
	void rejectsTamperedSignature() throws Exception {
		String rawJson = FinpayCallbackTestSupport.signedJson(samplePayload(), "merchant-key")
				.replace("alice", "mallory");

		assertThrows(InvalidPaymentSignatureException.class,
				() -> VALIDATOR.validateAndParse(rawJson, "merchant-key"));
	}

	private FinpayCallbackSignaturePayloadDTO samplePayload() {
		return FinpayCallbackSignaturePayloadDTO.builder()
				.order(FinpayCallbackOrderDTO.builder()
						.id("ORD-123")
						.amount(50_000L)
						.build())
				.customer(FinpayCallbackCustomerDTO.builder()
						.id("alice")
						.build())
				.build();
	}
}
