package finpay.payment.finpay.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinpayOrderDTO {

	private final String id;
	private final String amount;
	private final String currency;
	private final String description;
}
