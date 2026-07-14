package finpay.payment.web;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import finpay.payment.auth.application.AuthService;
import finpay.payment.auth.domain.dto.AuthResponseDTO;
import finpay.payment.auth.domain.dto.LoginRequestDTO;
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

	private final AuthService authService;
	private final UserService userService;

	@PostMapping("/register")
	public ResponseEntity<ApiResponse<UserResponseDTO>> register(
			@Valid @RequestBody CreateUserRequestDTO request) {
		UserResponseDTO user = userService.register(request.getUsername(), request.getPassword());
		return ResponseEntity
				.created(URI.create("/api/users/" + user.getId()))
				.body(ApiResponse.success(user, "User registered successfully"));
	}

	@PostMapping("/login")
	public ResponseEntity<ApiResponse<AuthResponseDTO>> login(
			@Valid @RequestBody LoginRequestDTO request) {
		AuthResponseDTO auth = authService.login(request.getUsername(), request.getPassword());
		return ResponseEntity.ok(ApiResponse.success(auth, "Login successful"));
	}

	@GetMapping("/verify")
	public ResponseEntity<ApiResponse<UserResponseDTO>> verify(@AuthenticationPrincipal Jwt jwt) {
		UserResponseDTO user = authService.verify(jwt.getSubject());
		return ResponseEntity.ok(ApiResponse.success(user, "Token verified successfully"));
	}
}
