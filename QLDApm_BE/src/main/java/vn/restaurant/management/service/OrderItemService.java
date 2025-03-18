package vn.restaurant.management.service;

import org.springframework.security.access.prepost.PreAuthorize;
import vn.restaurant.management.dto.request.OrderItemRequest;
import vn.restaurant.management.dto.response.OrderResponse;

public interface OrderItemService {

    @PreAuthorize("isAuthenticated()")
    OrderResponse addOrderItem(OrderItemRequest request);

    @PreAuthorize("isAuthenticated()")
    OrderResponse deleteOrderItem(String orderItemId);

    @PreAuthorize("isAuthenticated()")
    OrderResponse getOrderItemsByOrderId(String orderId);

    @PreAuthorize("isAuthenticated()")
    OrderResponse updateOrderItem(String orderItemId, OrderItemRequest updatedRequest);
}
