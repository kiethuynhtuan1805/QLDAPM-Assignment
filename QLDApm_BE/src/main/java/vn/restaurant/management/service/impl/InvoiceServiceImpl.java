package vn.restaurant.management.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import vn.restaurant.management.config.UserContext;
import vn.restaurant.management.dto.request.InvoiceRequest;
import vn.restaurant.management.dto.response.InvoiceResponse;
import vn.restaurant.management.entity.Invoice;
import vn.restaurant.management.entity.OrderItem;
import vn.restaurant.management.entity.Orders;
import vn.restaurant.management.entity.Payment;
import vn.restaurant.management.repository.InvoiceRepository;
import vn.restaurant.management.repository.OrderItemRepository;
import vn.restaurant.management.repository.OrderRepository;
import vn.restaurant.management.repository.PaymentRepository;
import vn.restaurant.management.service.InvoiceService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private UserContext userContext;

    @Override

    @PreAuthorize("isAuthenticated()")
    public InvoiceResponse updateInvoice(String id, InvoiceRequest request) {
        Invoice invoice = findInvoiceById(id);
        updateInvoiceDetails(invoice, request);
        return buildInvoiceResponse(invoice);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public void deleteInvoice(String id) {
        Invoice invoice = findInvoiceById(id);
        invoiceRepository.delete(invoice);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public InvoiceResponse getById(String id) {
        Invoice invoice = findInvoiceById(id);
        return buildInvoiceResponse(invoice);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public List<InvoiceResponse> getAll() {
        List<Invoice> invoices = invoiceRepository.findAll();
        return invoices.stream()
                .map(this::buildInvoiceResponse)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public List<InvoiceResponse> getInvoicesByCurrentUser(){
        // Tìm các đơn hàng của người dùng hiện tại
        List<Orders> userOrders = orderRepository.findByUserId(userContext.getUserId());

        // Lấy danh sách orderId từ các đơn hàng của người dùng
        List<String> orderIds = userOrders.stream()
                .map(Orders::getOrderId)
                .collect(Collectors.toList());

        // Tìm các hoá đơn có orderId trong danh sách orderIds
        List<Invoice> invoices = invoiceRepository.findByOrderIds(orderIds);

        // Chuyển đổi danh sách Invoice sang danh sách InvoiceResponse
        return invoices.stream()
                .map(this::buildInvoiceResponse)
                .collect(Collectors.toList());
    }

    private Invoice findInvoiceById(String id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hóa đơn với id: " + id));
    }

    private void updateInvoiceDetails(Invoice invoice, InvoiceRequest request) {
        invoice.setTotalAmount(request.getTotalAmount());
        invoiceRepository.save(invoice);
    }

    private InvoiceResponse buildInvoiceResponse(Invoice invoice) {
        Orders order = findOrderById(invoice.getOrderId());
        List<OrderItem> orderItems = findOrderItemsByOrderId(order.getOrderId());
        Payment payment = findPaymentById(invoice.getPaymentId());

        return InvoiceResponse.builder()
                .invoiceId(invoice.getInvoiceId())
                .usernameId(order.getUserId())
                .orderId(invoice.getOrderId())
                .paymentId(invoice.getPaymentId())
                .orderItems(orderItems)
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(payment.getPaymentStatus())
                .amountPaid(invoice.getTotalAmount().toString())
                .paymentDate(payment.getPaymentDate())
                .build();
    }

    private Orders findOrderById(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng với id: " + orderId));
    }

    private List<OrderItem> findOrderItemsByOrderId(String orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    private Payment findPaymentById(String paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thanh toán với id: " + paymentId));
    }
}
