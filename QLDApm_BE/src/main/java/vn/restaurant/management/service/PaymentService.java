package vn.restaurant.management.service;

import vn.restaurant.management.dto.request.PaymentRequest;
import vn.restaurant.management.dto.request.UpdatePaymentRequest;
import vn.restaurant.management.dto.response.PaymentResponse;

import java.util.List;

public interface PaymentService {
    PaymentResponse createPayment(PaymentRequest request);

    PaymentResponse updatePayment(String paymentId, PaymentRequest request);

    void deletePayment(String paymentId);

    PaymentResponse getPaymentId(String paymentId);

    List<PaymentResponse> getPaymentsByUser();

    List<PaymentResponse> getAllPayments();

    PaymentResponse updatePaymentByOrderId(String orderId, UpdatePaymentRequest request);

    PaymentResponse getPaymentByOrderId(String orderId);
}
