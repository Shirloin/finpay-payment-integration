package finpay.payment.auth.domain.dto;

import java.time.Instant;

import finpay.payment.user.domain.dto.UserResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {

	private String accessToken;
	private String tokenType;
	private Instant expiresAt;
	private UserResponseDTO user;
}
