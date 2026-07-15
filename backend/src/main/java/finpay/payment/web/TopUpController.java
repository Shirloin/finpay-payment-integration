package finpay.payment.web;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import finpay.payment.order.application.TopUpService;
import finpay.payment.order.domain.dto.OrderStatusResponseDTO;
import finpay.payment.order.domain.dto.TopUpRequestDTO;
import finpay.payment.order.domain.dto.TopUpResponseDTO;
import finpay.payment.order.domain.dto.TransactionResponseDTO;
import finpay.payment.shared.infrastructure.common.ApiResponse;
import finpay.payment.wallet.application.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TopUpController {

	private final TopUpService topUpService;
	private final WalletService walletService;

	@PostMapping("/api/topup")
	public ResponseEntity<ApiResponse<TopUpResponseDTO>> topUp(
			@AuthenticationPrincipal Jwt jwt,
			@Valid @RequestBody TopUpRequestDTO request) {
		TopUpResponseDTO response = topUpService.initiateTopUp(
				UUID.fromString(jwt.getSubject()),
				request.getAmount());
		return ResponseEntity.ok(ApiResponse.success(response, "Top up initiated successfully"));
	}

	@GetMapping("/api/balance")
	public ResponseEntity<ApiResponse<Long>> balance(@AuthenticationPrincipal Jwt jwt) {
		long balance = walletService.getBalance(UUID.fromString(jwt.getSubject()));
		return ResponseEntity.ok(ApiResponse.success(balance, "Balance retrieved successfully"));
	}

	@GetMapping("/api/transactions")
	public ResponseEntity<ApiResponse<List<TransactionResponseDTO>>> transactions(
			@AuthenticationPrincipal Jwt jwt) {
		List<TransactionResponseDTO> transactions = topUpService.listTransactions(
				UUID.fromString(jwt.getSubject()));
		return ResponseEntity.ok(ApiResponse.success(transactions, "Transactions retrieved successfully"));
	}

	@GetMapping("/api/orders/{orderId}")
	public ResponseEntity<ApiResponse<OrderStatusResponseDTO>> orderStatus(
			@AuthenticationPrincipal Jwt jwt,
			@PathVariable String orderId) {
		OrderStatusResponseDTO status = topUpService.getOrderStatus(
				UUID.fromString(jwt.getSubject()),
				orderId);
		return ResponseEntity.ok(ApiResponse.success(status, "Order status retrieved successfully"));
	}
}
