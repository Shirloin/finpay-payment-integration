package finpay.payment.order.domain;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import finpay.payment.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Order {

	@Id
	private UUID id;

	@Column(name = "order_id", nullable = false, unique = true, length = 30)
	private String orderId;

	@Column(nullable = false)
	private long amount;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private OrderStatus status;

	@Column(name = "payment_method", length = 50)
	private String paymentMethod;

	@Column(name = "payment_code", length = 30)
	private String paymentCode;

	@Column(name = "redirect_url", length = 320)
	private String redirectUrl;

	@Column(name = "expiry_link", length = 32)
	private String expiryLink;

	@Column(name = "response_code", length = 20)
	private String responseCode;

	@Column(name = "response_message", length = 500)
	private String responseMessage;

	@Column(name = "payment_status", length = 50)
	private String paymentStatus;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	public void applyInitiateResponse(
			String responseCode,
			String responseMessage,
			String paymentCode,
			String redirectUrl,
			String expiryLink) {
		this.responseCode = responseCode;
		this.responseMessage = responseMessage;
		this.paymentCode = paymentCode;
		this.redirectUrl = redirectUrl;
		this.expiryLink = expiryLink;
	}

	public void markPaid(String paymentMethod, String paymentStatus) {
		this.status = OrderStatus.PAID;
		this.paymentMethod = paymentMethod;
		this.paymentStatus = paymentStatus;
	}

	public void markFailed(String paymentMethod, String paymentStatus) {
		this.status = OrderStatus.FAIL;
		this.paymentMethod = paymentMethod;
		this.paymentStatus = paymentStatus;
	}
}
