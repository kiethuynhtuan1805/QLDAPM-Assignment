package vn.restaurant.management.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import vn.restaurant.management.config.UserContext;
import vn.restaurant.management.dto.request.PaymentRequest;
import vn.restaurant.management.dto.request.UpdatePaymentRequest;
import vn.restaurant.management.dto.response.PaymentResponse;
import vn.restaurant.management.entity.OrderItem;
import vn.restaurant.management.entity.Orders;
import vn.restaurant.management.entity.Payment;
import vn.restaurant.management.repository.OrderItemRepository;
import vn.restaurant.management.repository.OrderRepository;
import vn.restaurant.management.repository.PaymentRepository;
import vn.restaurant.management.service.PaymentService;
import vn.restaurant.management.utils.DateUtils;
import vn.restaurant.management.utils.GeneralUtils;

import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private UserContext userContext;

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public PaymentResponse createPayment(PaymentRequest request) {

        Orders order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Order không tồn tại"));

        if (order.getTotalPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Không có hóa đơn thanh toán vì tổng giá trị đơn hàng bằng hoặc nhỏ hơn 0.");
        }

        if(userContext.getUserId() == null){
            throw new IllegalStateException("Người dùng chưa đăng nhập hoặc không có quyền truy cập.");
        }

        Payment payment = Payment.builder()
                .paymentId(GeneralUtils.generateId())
                .orderId(request.getOrderId())
                .userId(userContext.getUserId())
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(request.getPaymentStatus())
                .amountPaid(order.getTotalPrice())
                .paymentDate(DateUtils.getCurrentDateTime())
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getOrderId());

        return buildPaymentResponse(savedPayment, orderItems);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public PaymentResponse updatePayment(String paymentId, PaymentRequest request) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment không tồn tại"));

        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setPaymentStatus(request.getPaymentStatus());
        payment.setAmountPaid(request.getAmountPaid());
        payment.setPaymentDate(DateUtils.getCurrentDateTime());

        Payment updatedPayment = paymentRepository.save(payment);
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(payment.getOrderId());

        return buildPaymentResponse(updatedPayment, orderItems);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public void deletePayment(String paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment không tồn tại"));

        paymentRepository.delete(payment);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public PaymentResponse getPaymentId(String paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment không tồn tại"));

        List<OrderItem> orderItems = orderItemRepository.findByOrderId(payment.getOrderId());

        return buildPaymentResponse(payment, orderItems);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public List<PaymentResponse> getPaymentsByUser() {
        // Lấy userId của người dùng hiện tại
        String userId = userContext.getUserId();
        if (userId == null) {
            throw new IllegalStateException("Người dùng chưa đăng nhập hoặc không có quyền truy cập.");
        }

        // Truy vấn danh sách các thanh toán theo userId
        List<Payment> payments = paymentRepository.findByUserId(userId);

        // Chuyển đổi danh sách Payment thành danh sách PaymentResponse
        return payments.stream()
                .map(payment -> {
                    List<OrderItem> orderItems = orderItemRepository.findByOrderId(payment.getOrderId());
                    return buildPaymentResponse(payment, orderItems);
                })
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public List<PaymentResponse> getAllPayments() {
        List<Payment> payments = paymentRepository.findAll();

        return payments.stream()
                .map(payment -> {
                    List<OrderItem> orderItems = orderItemRepository.findByOrderId(payment.getOrderId());
                    return buildPaymentResponse(payment, orderItems);
                })
                .collect(Collectors.toList());
    }

    @Override
    public PaymentResponse updatePaymentByOrderId(String orderId, UpdatePaymentRequest request){
        Payment payment = paymentRepository.findByOrderId(orderId)
                        .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy orderId"));
        payment.setPaymentStatus(request.getPaymentStatus());
        payment.setPaymentMethod(request.getPaymentMethod());
        paymentRepository.save(payment);
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order không tồn tại"));
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getOrderId());
        return buildPaymentResponse(payment, orderItems);
    }

    @Override
    public PaymentResponse getPaymentByOrderId(String orderId){
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy orderId"));
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order không tồn tại"));
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getOrderId());
        return buildPaymentResponse(payment, orderItems);
    }

    private PaymentResponse buildPaymentResponse(Payment payment, List<OrderItem> orderItems) {
        return PaymentResponse.builder()
                .paymentId(payment.getPaymentId())
                .orderId(payment.getOrderId())
                .userId(payment.getUserId())
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(payment.getPaymentStatus())
                .amountPaid(payment.getAmountPaid())
                .paymentDate(payment.getPaymentDate())
                .orderItems(orderItems)
                .build();
    }
}
