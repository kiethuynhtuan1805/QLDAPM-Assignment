package vn.restaurant.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.restaurant.management.entity.Payment;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
    List<Payment> findByUserId(String userId);

    Optional<Payment> findByOrderId(String orderId);
}
