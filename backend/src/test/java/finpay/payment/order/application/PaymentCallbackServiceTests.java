package finpay.payment.order.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import finpay.payment.order.domain.Order;
import finpay.payment.order.domain.OrderStatus;
import finpay.payment.order.infrastructure.OrderJpaRepository;
import finpay.payment.finpay.infrastructure.FinpayCallbackTestSupport;
import finpay.payment.finpay.infrastructure.FinpayApiClient;
import finpay.payment.finpay.infrastructure.FinpayProperties;
import finpay.payment.finpay.infrastructure.FinpaySignatureValidator;
import finpay.payment.finpay.dto.FinpayCallbackCustomerDTO;
import finpay.payment.finpay.dto.FinpayCallbackOrderDTO;
import finpay.payment.finpay.dto.FinpayCallbackPaymentDTO;
import finpay.payment.finpay.dto.FinpayCallbackRequestDTO;
import finpay.payment.finpay.dto.FinpayCallbackResponseDTO;
import finpay.payment.finpay.dto.FinpayCallbackResultDTO;
import finpay.payment.finpay.dto.FinpayCallbackSignaturePayloadDTO;
import finpay.payment.finpay.dto.FinpayCheckStatusDataDTO;
import finpay.payment.finpay.dto.FinpayCheckStatusResponseDTO;
import finpay.payment.finpay.dto.FinpaySourceOfFundsDTO;
import finpay.payment.user.domain.User;
import finpay.payment.wallet.application.WalletService;

@ExtendWith(MockitoExtension.class)
class PaymentCallbackServiceTests {

	@Mock
	private OrderJpaRepository orderRepository;

	@Mock
	private WalletService walletService;

	@Mock
	private FinpayApiClient finpayApiClient;

	@Mock
	private FinpayProperties finpayProperties;

	private final FinpaySignatureValidator signatureValidator = new FinpaySignatureValidator(
			tools.jackson.databind.json.JsonMapper.builder().build());
	private PaymentCallbackService service;

	@BeforeEach
	void setUp() {
		service = new PaymentCallbackService(
				orderRepository,
				walletService,
				finpayApiClient,
				signatureValidator,
				finpayProperties);
		when(finpayProperties.getMerchantKey()).thenReturn("merchant-key");
	}

	@Test
	void creditsWalletWhenPaymentIsCaptured() throws Exception {
		Order order = pendingOrder();
		String rawJson = callbackRequestJson("ORD-123");
		FinpayCheckStatusResponseDTO checkResponse = checkResponse("PAID", "finpaycode");

		when(orderRepository.findByOrderId("ORD-123")).thenReturn(Optional.of(order));
		when(finpayApiClient.checkStatus("ORD-123")).thenReturn(checkResponse);

		FinpayCallbackResponseDTO response = service.handleCallback(rawJson);

		assertEquals("2000000", response.getResponseCode());
		assertEquals(OrderStatus.PAID, order.getStatus());
		assertEquals("finpaycode", order.getPaymentMethod());
		verify(walletService).credit(order.getUser(), 50_000L);
	}

	@Test
	void skipsWalletCreditWhenOrderAlreadyPaid() throws Exception {
		Order order = pendingOrder();
		order.markPaid("finpaycode", "CAPTURED");
		when(orderRepository.findByOrderId("ORD-123")).thenReturn(Optional.of(order));

		service.handleCallback(callbackRequestJson("ORD-123"));

		verify(finpayApiClient, never()).checkStatus(any());
		verify(walletService, never()).credit(any(), eq(50_000L));
	}

	private Order pendingOrder() {
		User user = User.builder()
				.id(UUID.randomUUID())
				.username("alice")
				.password("hash")
				.build();
		return Order.builder()
				.id(UUID.randomUUID())
				.orderId("ORD-123")
				.amount(50_000L)
				.user(user)
				.status(OrderStatus.PENDING)
				.build();
	}

	private String callbackRequestJson(String orderId) throws Exception {
		return FinpayCallbackTestSupport.signedJson(
				FinpayCallbackSignaturePayloadDTO.builder()
						.order(FinpayCallbackOrderDTO.builder()
								.id(orderId)
								.amount(50_000L)
								.build())
						.customer(FinpayCallbackCustomerDTO.builder()
								.id("alice")
								.build())
						.build(),
				"merchant-key");
	}

	private FinpayCheckStatusResponseDTO checkResponse(String paymentStatus, String sourceOfFunds) {
		return FinpayCheckStatusResponseDTO.builder()
				.responseCode("2000000")
				.responseMessage("Success")
				.data(FinpayCheckStatusDataDTO.builder()
						.result(FinpayCallbackResultDTO.builder()
								.payment(FinpayCallbackPaymentDTO.builder()
										.status(paymentStatus)
										.build())
								.build())
						.sourceOfFunds(FinpaySourceOfFundsDTO.builder()
								.type(sourceOfFunds)
								.build())
						.build())
				.build();
	}
}
