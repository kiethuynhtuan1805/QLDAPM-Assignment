package vn.restaurant.management.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemRequest implements Serializable {
    private String orderId;
    private String dishId;
    private Integer quantity;
    private String specialRequests; //yêu cầu đặt biệt khi đặt món
}
