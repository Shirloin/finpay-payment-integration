package finpay.payment.finpay.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinpayUrlDTO {

	private final String backUrl;
	private final String successUrl;
	private final String failUrl;
	private final String callbackUrl;
	private final String threeDsResponseUrl;
}
