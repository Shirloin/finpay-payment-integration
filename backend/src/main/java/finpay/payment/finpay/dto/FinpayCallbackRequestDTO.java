package finpay.payment.finpay.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinpayCallbackRequestDTO {

	private FinpayCallbackOrderDTO order;
	private FinpayCallbackCustomerDTO customer;
	private FinpayCallbackMetaDTO meta;
	private FinpayCallbackCardDTO card;
	private FinpayCallbackResultDTO result;
	private String signature;
}
