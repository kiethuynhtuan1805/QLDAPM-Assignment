package vn.restaurant.management.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import vn.restaurant.management.config.UserContext;
import vn.restaurant.management.dto.response.OrderResponse;
import vn.restaurant.management.entity.OrderItem;
import vn.restaurant.management.entity.Orders;
import vn.restaurant.management.repository.OrderItemRepository;
import vn.restaurant.management.repository.OrderRepository;
import vn.restaurant.management.service.OrderService;
import vn.restaurant.management.service.ReservationService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private UserContext userContext;

    @Override
    @PreAuthorize("isAuthenticated() or hasAnyRole('ADMIN', 'STAFF', 'CUSTOMER')")
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::convertToOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("isAuthenticated() or hasAnyRole('ADMIN', 'STAFF', 'CUSTOMER')")
    public OrderResponse getOrderById(String id) {
        Orders order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + id));
        return convertToOrderResponse(order);
    }

    @Override
    @PreAuthorize("isAuthenticated() or hasAnyRole('ADMIN', 'STAFF', 'CUSTOMER')")
    public void deleteOrder(String id) {
        // Tìm Order trước khi xóa
        Orders order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + id));

        // Xóa tất cả OrderItem liên quan đến Order này
        orderItemRepository.deleteByOrderId(id);

        // Xóa chính Order
        orderRepository.deleteById(id);

        reservationService.deleteReservation(order.getReservationId());
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public List<OrderResponse> getOrdersByUserId(String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("User ID is required.");
        }

        // Tìm tất cả các đơn hàng của người dùng này theo userId
        List<Orders> orders = orderRepository.findByUserId(userId);

        // Chuyển đổi danh sách Orders thành danh sách OrderResponse
        return orders.stream()
                .map(this::convertToOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public List<OrderResponse> getOrdersByCurrentUser() {
        // Lấy userId của người dùng hiện tại
        String userId = userContext.getUserId();
        if (userId == null) {
            throw new IllegalStateException("Người dùng chưa đăng nhập hoặc không có quyền truy cập.");
        }

        // Tìm tất cả các đơn hàng của người dùng này
        List<Orders> orders = orderRepository.findByUserId(userId);

        // Chuyển đổi danh sách Orders thành danh sách OrderResponse
        return orders.stream()
                .map(this::convertToOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public List<OrderResponse> getOrdersByTableId(String tableId) {
        return orderRepository.findByTableId(tableId).stream()
                .map(this::convertToOrderResponse)
                .collect(Collectors.toList());
    }

    private OrderResponse convertToOrderResponse(Orders order) {
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getOrderId());
        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .userId(order.getUserId())
                .reservationId(order.getReservationId())
                .orderDate(order.getOrderDate())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus())
                .orderItems(orderItems)
                .build();
    }
}
