package vn.restaurant.management.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import vn.restaurant.management.dto.request.OrderItemRequest;
import vn.restaurant.management.dto.response.OrderResponse;
import vn.restaurant.management.entity.*;
import vn.restaurant.management.repository.*;
import vn.restaurant.management.service.OrderItemService;
import vn.restaurant.management.utils.GeneralUtils;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderItemServiceImpl implements OrderItemService {

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;


    @Override
    public OrderResponse addOrderItem(OrderItemRequest request) {
        // Lấy thông tin món ăn từ Dish
        Dish dish = dishRepository.findById(request.getDishId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy món ăn"));
        if(request.getQuantity() <= 0){
            throw new IllegalArgumentException("Số lượng không được nhỏ hơn 0");
        }

        // Tính tổng giá trị của món trong OrderItem
        BigDecimal itemTotal = dish.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));

        // Tạo và lưu OrderItem
        OrderItem orderItem = OrderItem.builder()
                .orderItemId(GeneralUtils.generateId())
                .orderId(request.getOrderId())
                .dishId(request.getDishId())
                .quantity(request.getQuantity())
                .specialRequests(request.getSpecialRequests())
                .build();
        orderItemRepository.save(orderItem);

        // Cập nhật totalPrice của Order
        Orders order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Order"));
        order.setTotalPrice(order.getTotalPrice().add(itemTotal));
        orderRepository.save(order);

        Payment payment = paymentRepository.findByOrderId(request.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy payment"));
        payment.setAmountPaid(order.getTotalPrice());
        paymentRepository.save(payment);

        Invoice invoice = invoiceRepository.findByOrderId(request.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy invoice"));
        invoice.setTotalAmount(order.getTotalPrice());
        invoiceRepository.save(invoice);

        // Lấy lại tất cả OrderItems sau khi thêm
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getOrderId());

        // Trả về OrderResponse với danh sách OrderItems và tổng giá đã cập nhật
        return convertToOrderResponse(order, orderItems);
    }


    @Override
    public OrderResponse deleteOrderItem(String orderItemId) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy order item"));

        Dish dish = dishRepository.findById(orderItem.getDishId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy món ăn"));

        // Tính tổng giá trị của OrderItem
        BigDecimal itemTotal = dish.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity()));

        // Xóa OrderItem
        orderItemRepository.deleteById(orderItemId);

        // Cập nhật totalPrice của Order
        Orders order = orderRepository.findById(orderItem.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Order"));
        order.setTotalPrice(order.getTotalPrice().subtract(itemTotal));
        orderRepository.save(order);

        Payment payment = paymentRepository.findByOrderId(orderItem.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy payment"));
        payment.setAmountPaid(order.getTotalPrice());
        paymentRepository.save(payment);

        Invoice invoice = invoiceRepository.findByOrderId(orderItem.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy invoice"));
        invoice.setTotalAmount(order.getTotalPrice());
        invoiceRepository.save(invoice);

        // Lấy danh sách OrderItems sau khi xóa và trả về OrderResponse
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getOrderId());
        return convertToOrderResponse(order, orderItems);
    }

    @Override
    public OrderResponse getOrderItemsByOrderId(String orderId) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Order"));

        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
        return convertToOrderResponse(order, orderItems);
    }

    @Override
    public OrderResponse updateOrderItem(String orderItemId, OrderItemRequest updatedRequest) {
        
        OrderItem existingOrderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy orderItem"));

        // Lấy thông tin món ăn từ Dish
        Dish dish = dishRepository.findById(updatedRequest.getDishId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy món ăn"));

        // Kiểm tra số lượng hợp lệ
        if (updatedRequest.getQuantity() <= 0) {
            throw new IllegalArgumentException("Số lượng không được nhỏ hơn 0");
        }

        // Tính lại tổng giá trị của OrderItem
        BigDecimal oldItemTotal = dish.getPrice().multiply(BigDecimal.valueOf(existingOrderItem.getQuantity()));
        BigDecimal newItemTotal = dish.getPrice().multiply(BigDecimal.valueOf(updatedRequest.getQuantity()));

        // Cập nhật các thuộc tính của OrderItem
        existingOrderItem.setDishId(updatedRequest.getDishId());
        existingOrderItem.setQuantity(updatedRequest.getQuantity());
        existingOrderItem.setSpecialRequests(updatedRequest.getSpecialRequests());

        // Lưu OrderItem đã cập nhật
        orderItemRepository.save(existingOrderItem);

        // Cập nhật totalPrice của Order
        Orders order = orderRepository.findById(existingOrderItem.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy order"));
        order.setTotalPrice(order.getTotalPrice().subtract(oldItemTotal).add(newItemTotal));
        orderRepository.save(order);

        Payment payment = paymentRepository.findByOrderId(existingOrderItem.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy payment"));
        payment.setAmountPaid(order.getTotalPrice());
        paymentRepository.save(payment);

        Invoice invoice = invoiceRepository.findByOrderId(existingOrderItem.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy invoice"));
        invoice.setTotalAmount(order.getTotalPrice());
        invoiceRepository.save(invoice);

        // Lấy danh sách OrderItems sau khi cập nhật
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getOrderId());

        // Trả về OrderResponse với danh sách OrderItems đã cập nhật và tổng giá mới
        return convertToOrderResponse(order, orderItems);
    }

    private OrderResponse convertToOrderResponse(Orders order, List<OrderItem> orderItems) {
        BigDecimal totalPrice = orderItems.stream()
                .map(item -> {
                    Dish dish = dishRepository.findById(item.getDishId())
                            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy món ăn"));
                    return dish.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add); // Tính tổng giá từ các OrderItem

        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .userId(order.getUserId())
                .reservationId(order.getReservationId())
                .orderDate(order.getOrderDate())
                .totalPrice(totalPrice) // Cập nhật totalPrice từ danh sách OrderItem
                .status(order.getStatus())
                .orderItems(orderItems)
                .build();
    }
}
