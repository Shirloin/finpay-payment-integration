package finpay.payment.finpay.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinpayCallbackSignaturePayloadDTO {

	private final FinpayCallbackOrderDTO order;
	private final FinpayCallbackCustomerDTO customer;
	private final FinpayCallbackMetaDTO meta;
	private final FinpayCallbackCardDTO card;
	private final FinpayCallbackResultDTO result;
}
