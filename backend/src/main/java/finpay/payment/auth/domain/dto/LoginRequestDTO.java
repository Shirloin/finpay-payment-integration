package finpay.payment.auth.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {

	@NotBlank(message = "username is required")
	private String username;

	@NotBlank(message = "password is required")
	private String password;
}
