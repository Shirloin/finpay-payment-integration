package finpay.payment.user.domain.dto;

import java.time.Instant;
import java.util.UUID;

import finpay.payment.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {

	private UUID id;
	private String username;
	private Instant createdAt;

	public static UserResponseDTO from(User user) {
		return UserResponseDTO.builder()
				.id(user.getId())
				.username(user.getUsername())
				.createdAt(user.getCreatedAt())
				.build();
	}
}
