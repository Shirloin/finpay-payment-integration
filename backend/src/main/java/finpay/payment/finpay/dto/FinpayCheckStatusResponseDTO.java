package finpay.payment.finpay.dto;

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
public class FinpayCheckStatusResponseDTO {

	private static final String SUCCESS_CODE = "2000000";
	private static final String PAID_STATUS = "PAID";

	private String responseCode;
	private String responseMessage;
	private FinpayCheckStatusDataDTO data;
	private Double processingTime;

	public boolean isSuccess() {
		return SUCCESS_CODE.equals(responseCode);
	}

	public String paymentStatus() {
		if (data == null || data.getResult() == null || data.getResult().getPayment() == null) {
			return null;
		}
		return data.getResult().getPayment().getStatus();
	}

	public String sourceOfFundsType() {
		if (data == null || data.getSourceOfFunds() == null) {
			return null;
		}
		return data.getSourceOfFunds().getType();
	}

	public boolean isPaid() {
		return PAID_STATUS.equalsIgnoreCase(paymentStatus());
	}
}
