package vn.restaurant.management.dto.request;

import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InvoiceRequest {
    private String orderId;
    private String paymentId;
    private BigDecimal totalAmount;
}
