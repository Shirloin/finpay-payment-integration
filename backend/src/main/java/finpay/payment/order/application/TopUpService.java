package finpay.payment.order.application;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import finpay.payment.order.domain.Order;
import finpay.payment.order.domain.OrderStatus;
import finpay.payment.order.domain.dto.OrderStatusResponseDTO;
import finpay.payment.order.domain.dto.TopUpResponseDTO;
import finpay.payment.order.domain.dto.TransactionResponseDTO;
import finpay.payment.order.infrastructure.OrderJpaRepository;
import finpay.payment.shared.infrastructure.exception.FinpayApiException;
import finpay.payment.shared.infrastructure.exception.OrderNotFoundException;
import finpay.payment.finpay.infrastructure.FinpayApiClient;
import finpay.payment.finpay.infrastructure.FinpayProperties;
import finpay.payment.finpay.infrastructure.FinpayRequestFactory;
import finpay.payment.finpay.dto.FinpayInitiateRequestDTO;
import finpay.payment.finpay.dto.FinpayInitiateResponseDTO;
import finpay.payment.user.domain.User;
import finpay.payment.user.infrastructure.UserJpaRepository;
import finpay.payment.wallet.application.WalletService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TopUpService {

	private final OrderJpaRepository orderRepository;
	private final UserJpaRepository userRepository;
	private final WalletService walletService;
	private final FinpayApiClient finpayApiClient;
	private final FinpayRequestFactory finpayRequestFactory;
	private final FinpayProperties finpayProperties;

	@Transactional(noRollbackFor = FinpayApiException.class)
	public TopUpResponseDTO initiateTopUp(UUID userId, long amount) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new OrderNotFoundException(userId.toString()));

		String orderId = finpayRequestFactory.generateOrderId();
		Order order = orderRepository.save(Order.builder()
				.id(UUID.randomUUID())
				.orderId(orderId)
				.amount(amount)
				.user(user)
				.status(OrderStatus.PENDING)
				.build());

		FinpayInitiateRequestDTO requestBody = finpayRequestFactory.buildInitiateRequest(
				user,
				orderId,
				amount,
				finpayProperties);
		FinpayInitiateResponseDTO response = finpayApiClient.initiate(requestBody);

		if (!response.isSuccess()) {
			order.markFailed(null, response.getResponseMessage());
			throw new FinpayApiException(response.getResponseMessage());
		}

		order.applyInitiateResponse(
				response.getResponseCode(),
				response.getResponseMessage(),
				response.getPaymentCode(),
				response.getRedirectUrl(),
				response.getExpiryLink());

		return TopUpResponseDTO.builder()
				.orderId(order.getOrderId())
				.amount(order.getAmount())
				.paymentCode(order.getPaymentCode())
				.redirectUrl(order.getRedirectUrl())
				.expiryLink(order.getExpiryLink())
				.status(order.getStatus().name())
				.createdAt(order.getCreatedAt())
				.build();
	}

	@Transactional(readOnly = true)
	public List<TransactionResponseDTO> listTransactions(UUID userId) {
		return orderRepository.findByUser_IdOrderByCreatedAtDesc(userId).stream()
				.map(TransactionResponseDTO::from)
				.toList();
	}

	@Transactional(readOnly = true)
	public OrderStatusResponseDTO getOrderStatus(UUID userId, String orderId) {
		Order order = orderRepository.findByOrderId(orderId)
				.filter(existing -> existing.getUser().getId().equals(userId))
				.orElseThrow(() -> new OrderNotFoundException(orderId));
		long balance = walletService.getBalance(userId);
		return OrderStatusResponseDTO.of(order, balance);
	}
}
