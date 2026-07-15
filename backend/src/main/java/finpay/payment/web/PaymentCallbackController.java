package finpay.payment.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import finpay.payment.order.application.PaymentCallbackService;
import finpay.payment.finpay.dto.FinpayCallbackRequestDTO;
import finpay.payment.finpay.dto.FinpayCallbackResponseDTO;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments/finpay")
@RequiredArgsConstructor
public class PaymentCallbackController {

	private final PaymentCallbackService paymentCallbackService;

	@PostMapping("/callback")
	public ResponseEntity<FinpayCallbackResponseDTO> callback(@RequestBody String rawJson) {
		return ResponseEntity.ok(paymentCallbackService.handleCallback(rawJson));
	}
}
