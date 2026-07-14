package finpay.payment.user.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequestDTO {

	@NotBlank(message = "username is required")
	@Size(min = 3, max = 50, message = "username must be between 3 and 50 characters")
	private String username;

	@NotBlank(message = "password is required")
	@Size(min = 6, max = 200, message = "password must be between 6 and 200 characters")
	private String password;
}
