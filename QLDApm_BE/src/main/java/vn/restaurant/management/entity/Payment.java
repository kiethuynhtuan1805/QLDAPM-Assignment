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
@Table(name="Payment")
public class Payment implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "payment_id", length = 20)
    private String paymentId;

    @Column(name = "order_id", length = 20)
    private String orderId;

    @Column(name = "user_id", length = 20)
    private String userId;

    @Column(name = "payment_method", length = 100)
    private String paymentMethod;

    @Column(name = "payment_status", length = 100)
    private String paymentStatus;

    @Column(name = "amount_paid", precision = 10, scale = 2)
    private BigDecimal amountPaid;

    @Column(name = "payment_date", updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date paymentDate;
}
