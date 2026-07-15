package finpay.payment.finpay.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FinpayCallbackResponseDTO {

	private static final String SUCCESS_CODE = "2000000";

	private final String responseCode;
	private final String responseMessage;
	private final Double processingTime;

	public static FinpayCallbackResponseDTO success() {
		return FinpayCallbackResponseDTO.builder()
				.responseCode(SUCCESS_CODE)
				.responseMessage("Success")
				.build();
	}
}
