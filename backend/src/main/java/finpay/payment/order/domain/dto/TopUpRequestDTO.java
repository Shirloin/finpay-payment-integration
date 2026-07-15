package finpay.payment.order.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TopUpRequestDTO {

	@NotNull
	@Min(10_000)
	@Max(10_000_000)
	private Long amount;
}
