package finpay.payment.finpay.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FinpayErrorResponseDTO {

	private String responseCode;
	private String responseMessage;
	private Double processingTime;
	private String traceId;
}
