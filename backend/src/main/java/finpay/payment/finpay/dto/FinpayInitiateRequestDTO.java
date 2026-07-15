package finpay.payment.finpay.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinpayInitiateRequestDTO {

	private final FinpayOrderDTO order;
	private final FinpayCustomerDTO customer;
	private final FinpayUrlDTO url;
}
