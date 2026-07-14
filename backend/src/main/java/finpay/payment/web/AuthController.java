package finpay.payment.web;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import finpay.payment.shared.infrastructure.common.ApiResponse;
import finpay.payment.user.application.UserService;
import finpay.payment.user.domain.dto.CreateUserRequestDTO;
import finpay.payment.user.domain.dto.UserResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final UserService userService;

	@PostMapping("/register")
	public ResponseEntity<ApiResponse<UserResponseDTO>> register(
			@Valid @RequestBody CreateUserRequestDTO request) {
		UserResponseDTO user = userService.register(request.getUsername(), request.getPassword());
		return ResponseEntity
				.created(URI.create("/api/users/" + user.getId()))
				.body(ApiResponse.success(user, "User registered successfully"));
	}
}
