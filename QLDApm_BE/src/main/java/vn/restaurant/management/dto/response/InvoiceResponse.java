package vn.restaurant.management.dto.response;

import lombok.*;
import vn.restaurant.management.entity.OrderItem;

import java.util.Date;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InvoiceResponse {
    private String invoiceId;
    private String usernameId;
    private String orderId;
    private String paymentId;
    private List<OrderItem> orderItems;
    private String paymentMethod;
    private String paymentStatus;
    private String amountPaid;
    private Date paymentDate;
}
