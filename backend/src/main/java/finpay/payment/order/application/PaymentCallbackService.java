package finpay.payment.order.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import finpay.payment.order.domain.Order;
import finpay.payment.order.domain.OrderStatus;
import finpay.payment.order.infrastructure.OrderJpaRepository;
import finpay.payment.shared.infrastructure.exception.InvalidPaymentSignatureException;
import finpay.payment.shared.infrastructure.exception.OrderNotFoundException;
import finpay.payment.finpay.infrastructure.FinpayApiClient;
import finpay.payment.finpay.infrastructure.FinpayProperties;
import finpay.payment.finpay.infrastructure.FinpaySignatureValidator;
import finpay.payment.finpay.dto.FinpayCallbackRequestDTO;
import finpay.payment.finpay.dto.FinpayCallbackResponseDTO;
import finpay.payment.finpay.dto.FinpayCheckStatusResponseDTO;
import finpay.payment.wallet.application.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentCallbackService {

	private final OrderJpaRepository orderRepository;
	private final WalletService walletService;
	private final FinpayApiClient finpayApiClient;
	private final FinpaySignatureValidator signatureValidator;
	private final FinpayProperties finpayProperties;

	@Transactional
	public FinpayCallbackResponseDTO handleCallback(String rawJson) {
		log.info("Finpay callback received");

		FinpayCallbackRequestDTO request = signatureValidator.validateAndParse(
				rawJson,
				finpayProperties.getMerchantKey());
		log.debug("Finpay callback signature validated");

		if (request.getOrder() == null || request.getOrder().getId() == null) {
			log.warn("Finpay callback rejected: missing order id in payload");
			throw new InvalidPaymentSignatureException();
		}

		String orderId = request.getOrder().getId();
		log.info("Processing Finpay callback for orderId={}", orderId);

		Order order = orderRepository.findByOrderId(orderId)
				.orElseThrow(() -> {
					log.warn("Finpay callback failed: order not found, orderId={}", orderId);
					return new OrderNotFoundException(orderId);
				});

		log.info("Order loaded: orderId={}, status={}, amount={}", orderId, order.getStatus(), order.getAmount());

		if (order.getStatus() == OrderStatus.PAID) {
			log.info("Finpay callback skipped: order already paid, orderId={}", orderId);
			return FinpayCallbackResponseDTO.success();
		}

		log.info("Checking payment status with Finpay for orderId={}", orderId);
		FinpayCheckStatusResponseDTO checkResponse = finpayApiClient.checkStatus(orderId);
		log.info(
				"Finpay check status response: orderId={}, responseCode={}, responseMessage={}, paymentStatus={}, sourceOfFunds={}",
				orderId,
				checkResponse.getResponseCode(),
				checkResponse.getResponseMessage(),
				checkResponse.paymentStatus(),
				checkResponse.sourceOfFundsType());

		if (!checkResponse.isSuccess()) {
			log.warn(
					"Finpay check status failed: orderId={}, responseCode={}, responseMessage={}",
					orderId,
					checkResponse.getResponseCode(),
					checkResponse.getResponseMessage());
			order.markFailed(null, checkResponse.getResponseMessage());
			log.info("Order marked failed after check status error: orderId={}", orderId);
			return FinpayCallbackResponseDTO.success();
		}

		String paymentMethod = checkResponse.sourceOfFundsType();

		if (checkResponse.isPaid()) {
			log.info(
					"Payment captured: orderId={}, paymentMethod={}, paymentStatus={}, amount={}",
					orderId,
					paymentMethod,
					checkResponse.paymentStatus(),
					order.getAmount());
			order.markPaid(paymentMethod, checkResponse.paymentStatus());
			walletService.credit(order.getUser(), order.getAmount());
			log.info("Wallet credited: orderId={}, userId={}, amount={}", orderId, order.getUser().getId(),
					order.getAmount());
		} else {
			log.warn(
					"Payment not captured: orderId={}, paymentMethod={}, paymentStatus={}",
					orderId,
					paymentMethod,
					checkResponse.paymentStatus());
			order.markFailed(paymentMethod, checkResponse.paymentStatus());
			log.info("Order marked failed: orderId={}", orderId);
		}

		log.info("Finpay callback completed: orderId={}, finalStatus={}", orderId, order.getStatus());
		return FinpayCallbackResponseDTO.success();
	}
}
