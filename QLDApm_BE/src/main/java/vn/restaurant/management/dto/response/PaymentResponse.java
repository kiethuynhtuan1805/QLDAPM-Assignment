package vn.restaurant.management.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.restaurant.management.entity.OrderItem;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private String paymentId;
    private String orderId;
    private String userId;
    private String paymentMethod;
    private String paymentStatus;
    private BigDecimal amountPaid;
    private Date paymentDate;
    private List<OrderItem> orderItems;
}
