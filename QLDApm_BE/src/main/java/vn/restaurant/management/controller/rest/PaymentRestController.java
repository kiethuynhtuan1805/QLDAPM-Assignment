package vn.restaurant.management.controller.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.restaurant.management.constant.Common;
import vn.restaurant.management.dto.request.PaymentRequest;
import vn.restaurant.management.dto.request.UpdatePaymentRequest;
import vn.restaurant.management.dto.response.DataResponse;
import vn.restaurant.management.dto.response.PaymentResponse;
import vn.restaurant.management.service.PaymentService;

import java.util.List;

@CrossOrigin("*")
@RestController
@Slf4j
@RequestMapping(Common.API_PATH + Common.API_VERSION)
public class PaymentRestController {

    @Autowired
    private PaymentService paymentService;

    /**
     * Lấy danh sách tất cả các thanh toán của người dùng hiện tại.
     * @return Danh sách các thanh toán
     */
    @GetMapping("/payments")
    public DataResponse<List<PaymentResponse>> getAllPayments() {
        DataResponse<List<PaymentResponse>> response;
        try {
            List<PaymentResponse> data = paymentService.getAllPayments();
            response = DataResponse.successResponse(data, "Danh sách các thanh toán");
        } catch (Exception ex) {
            log.error("Get all payments failed, cause={}", ex.getMessage());
            response = DataResponse.failResponse(ex.getMessage());
        }
        return response;
    }

    /**
     * Lấy thông tin chi tiết thanh toán theo ID.
     * @param id ID của thanh toán
     * @return Thông tin chi tiết thanh toán
     */
    @GetMapping("/payment/{id}")
    public DataResponse<PaymentResponse> getPaymentById(@PathVariable String id) {
        DataResponse<PaymentResponse> response;
        try {
            PaymentResponse payment = paymentService.getPaymentId(id);
            response = DataResponse.successResponse(payment, "Thông tin chi tiết thanh toán");
        } catch (Exception ex) {
            log.error("Get payment by id failed, cause={}", ex.getMessage());
            response = DataResponse.failResponse(ex.getMessage());
        }
        return response;
    }

    /**
     * Lấy danh sách tất cả các thanh toán của người dùng hiện tại.
     * @return Danh sách các thanh toán của người dùng hiện tại
     */
    @GetMapping("/payments/user")
    public DataResponse<List<PaymentResponse>> getPaymentsByUser() {
        DataResponse<List<PaymentResponse>> response;
        try {
            List<PaymentResponse> payments = paymentService.getPaymentsByUser();
            response = DataResponse.successResponse(payments, "Danh sách các thanh toán của người dùng hiện tại");
        } catch (Exception ex) {
            log.error("Get payments by user failed, cause={}", ex.getMessage());
            response = DataResponse.failResponse(ex.getMessage());
        }
        return response;
    }


    /**
     * Tạo thanh toán mới.
     * @param request Yêu cầu tạo thanh toán mới
     * @return Thông tin thanh toán mới tạo
     */
//    @PostMapping("/payment")
//    public DataResponse<PaymentResponse> createPayment(@RequestBody PaymentRequest request) {
//        DataResponse<PaymentResponse> response;
//        try {
//            PaymentResponse payment = paymentService.createPayment(request);
//            response = DataResponse.successResponse(payment, "Tạo thanh toán thành công");
//        } catch (Exception ex) {
//            log.error("Create payment failed, cause={}", ex.getMessage());
//            response = DataResponse.failResponse(ex.getMessage());
//        }
//        return response;
//    }

    /**
     * Cập nhật thông tin thanh toán.
     * @param id ID của thanh toán cần cập nhật
     * @param request Yêu cầu cập nhật thanh toán
     * @return Thông tin thanh toán đã cập nhật
     */
    @PutMapping("/payment/{id}")
    public DataResponse<PaymentResponse> updatePayment(@PathVariable String id, @RequestBody PaymentRequest request) {
        DataResponse<PaymentResponse> response;
        try {
            PaymentResponse payment = paymentService.updatePayment(id, request);
            response = DataResponse.successResponse(payment, "Cập nhật thanh toán thành công");
        } catch (Exception ex) {
            log.error("Update payment failed, cause={}", ex.getMessage());
            response = DataResponse.failResponse(ex.getMessage());
        }
        return response;
    }

    /**
     * Xóa thanh toán theo ID.
     * @param id ID của thanh toán
     * @return Kết quả xóa thanh toán
     */
    @DeleteMapping("/payment/{id}")
    public DataResponse<Void> deletePayment(@PathVariable String id) {
        DataResponse<Void> response;
        try {
            paymentService.deletePayment(id);
            response = DataResponse.successResponse("Xóa thanh toán thành công");
        } catch (Exception ex) {
            log.error("Delete payment failed, cause={}", ex.getMessage());
            response = DataResponse.failResponse(ex.getMessage());
        }
        return response;
    }

    @PutMapping("/payment/orderId/{id}")
    public DataResponse<PaymentResponse> updatePaymentOrderId(@PathVariable String id, @RequestBody UpdatePaymentRequest request) {
        DataResponse<PaymentResponse> response;
        try {
            PaymentResponse data = paymentService.updatePaymentByOrderId(id, request);
            response = DataResponse.successResponse(data,"Cập nhật trạng thái payment thành công");
        } catch (Exception ex) {
            log.error("Update payment orderId failed, cause={}", ex.getMessage());
            response = DataResponse.failResponse(ex.getMessage());
        }
        return response;
    }

    @GetMapping("/payment/orderId/{id}")
    public DataResponse<PaymentResponse> getPaymentOrderId(@PathVariable String id) {
        DataResponse<PaymentResponse> response;
        try {
            PaymentResponse data = paymentService.getPaymentByOrderId(id);
            response = DataResponse.successResponse(data, "Payment của userid: " +id);
        }catch (Exception ex) {
            log.error("Get payment orderId failed, cause={}", ex.getMessage());
            response = DataResponse.failResponse(ex.getMessage());
        }
        return response;
    }
}
