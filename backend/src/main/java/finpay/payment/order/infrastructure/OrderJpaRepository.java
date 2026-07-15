package finpay.payment.order.infrastructure;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import finpay.payment.order.domain.Order;

public interface OrderJpaRepository extends JpaRepository<Order, UUID> {

	Optional<Order> findByOrderId(String orderId);

	List<Order> findByUser_IdOrderByCreatedAtDesc(UUID userId);
}
