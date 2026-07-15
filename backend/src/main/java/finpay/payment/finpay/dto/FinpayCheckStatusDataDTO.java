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
public class FinpayCheckStatusDataDTO {

	private FinpayCallbackCustomerDTO customer;
	private FinpayCallbackOrderDTO order;
	private FinpayCallbackCardDTO card;
	private FinpayCallbackMetaDTO meta;
	private FinpayCallbackResultDTO result;
	private FinpaySourceOfFundsDTO sourceOfFunds;
}
