package finpay.payment.finpay.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinpayInitiateResponseDTO {

	private static final String SUCCESS_CODE = "2000000";

	private String responseCode;
	private String responseMessage;
	private String paymentCode;
	@JsonProperty("redirecturl")
	private String redirectUrl;
	private String expiryLink;
	private Double processingTime;
	private String appurl;
	private String imageurl;

	public boolean isSuccess() {
		return SUCCESS_CODE.equals(responseCode);
	}
}
