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
@Table(name="Invoice")
public class Invoice implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "invoice_id", length = 20)
    private String invoiceId;

    @Column(name = "order_id", length = 20)
    private String orderId;

    @Column(name = "payment_id", length = 20)
    private String paymentId;

    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "invoice_date", updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date invoiceDate;
}
