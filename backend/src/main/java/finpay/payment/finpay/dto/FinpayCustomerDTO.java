package finpay.payment.finpay.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinpayCustomerDTO {

	private final String id;
	private final String email;
	private final String firstName;
	private final String lastName;
	private final String mobilePhone;
	private final String phone;
	private final String taxRegistrationId;
}
