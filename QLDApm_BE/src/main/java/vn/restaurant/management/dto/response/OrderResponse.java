package vn.restaurant.management.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.restaurant.management.entity.OrderItem;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderResponse {
    private String orderId;
    private String userId;
    private String reservationId;
    private Date orderDate;
    private BigDecimal totalPrice;
    private String status;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<OrderItem> orderItems;
}
