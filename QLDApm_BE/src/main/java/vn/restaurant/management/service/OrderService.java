package vn.restaurant.management.service;

import vn.restaurant.management.dto.response.OrderResponse;

import java.util.List;

public interface OrderService {

    List<OrderResponse> getAllOrders();

    OrderResponse getOrderById(String id);

    void deleteOrder(String id);

    List<OrderResponse> getOrdersByUserId(String userId);

    List<OrderResponse> getOrdersByCurrentUser();

    List<OrderResponse> getOrdersByTableId(String tableId);
}
