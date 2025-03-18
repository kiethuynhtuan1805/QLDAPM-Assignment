package vn.restaurant.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.restaurant.management.entity.OrderItem;

import jakarta.transaction.Transactional;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, String> {
    List<OrderItem> findByOrderId(String orderId);

    @Transactional
    @Modifying
    @Query("DELETE FROM OrderItem oi WHERE oi.orderId = :orderId")
    void deleteByOrderId(String orderId);
}
