package vn.restaurant.management.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="order_item")
public class OrderItem implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "order_item_id", length = 10)
    private String orderItemId;

    @Column(name = "order_id", length = 10)
    private String orderId;  // Dùng trực tiếp khóa ngoại order_id dưới dạng String

    @Column(name = "dish_id", length = 10)
    private String dishId;  // Dùng trực tiếp khóa ngoại dish_id dưới dạng String

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "special_requests", columnDefinition = "TEXT")
    private String specialRequests;
}
