package vn.restaurant.management.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="Orders")
public class Orders implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "order_id", length = 10)
    private String orderId;

    @Column(name = "user_id", length = 10)
    private String userId;

    @Column(name = "reservation_id", length = 10)
    private String reservationId;

    @Column(name = "order_date", updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date orderDate;

    @Column(name = "total_price", precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "status", length = 100)
    private String status;

    @Column(name = "date_created", updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date dateCreated;

    @Column(name = "date_updated", insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Date dateUpdated;
}
