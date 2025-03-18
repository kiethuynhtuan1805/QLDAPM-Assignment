package vn.restaurant.management.controller.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.restaurant.management.constant.Common;
import vn.restaurant.management.dto.request.InvoiceRequest;
import vn.restaurant.management.dto.response.DataResponse;
import vn.restaurant.management.dto.response.InvoiceResponse;
import vn.restaurant.management.service.InvoiceService;

import java.util.List;

@CrossOrigin("*")
@RestController
@Slf4j
@RequestMapping(Common.API_PATH + Common.API_VERSION)
public class InvoiceRestController {

    @Autowired
    private InvoiceService invoiceService;

    /**
     * Lấy danh sách tất cả các hóa đơn.
     * @return Danh sách các hóa đơn
     */
    @GetMapping("/invoices")
    public DataResponse<List<InvoiceResponse>> getAllInvoices() {
        DataResponse<List<InvoiceResponse>> response;
        try {
            List<InvoiceResponse> data = invoiceService.getAll();
            response = DataResponse.successResponse(data, "Danh sách các hóa đơn");
        } catch (Exception ex) {
            log.error("Get all invoices failed, cause={}", ex.getMessage());
            response = DataResponse.failResponse(ex.getMessage());
        }
        return response;
    }

    /**
     * Lấy thông tin chi tiết hóa đơn theo ID.
     * @param id ID của hóa đơn
     * @return Thông tin chi tiết hóa đơn
     */
    @GetMapping("/invoice/{id}")
    public DataResponse<InvoiceResponse> getInvoiceById(@PathVariable String id) {
        DataResponse<InvoiceResponse> response;
        try {
            InvoiceResponse invoice = invoiceService.getById(id);
            response = DataResponse.successResponse(invoice, "Thông tin chi tiết hóa đơn");
        } catch (Exception ex) {
            log.error("Get invoice by id failed, cause={}", ex.getMessage());
            response = DataResponse.failResponse(ex.getMessage());
        }
        return response;
    }

    /**
     * Lấy danh sách tất cả các hóa đơn của người dùng hiện tại.
     * @return Danh sách các hóa đơn của người dùng hiện tại
     */
    @GetMapping("/invoices/user")
    public DataResponse<List<InvoiceResponse>> getInvoicesByCurrentUser() {
        DataResponse<List<InvoiceResponse>> response;
        try {
            List<InvoiceResponse> invoices = invoiceService.getInvoicesByCurrentUser();
            response = DataResponse.successResponse(invoices, "Danh sách các hóa đơn của người dùng hiện tại");
        } catch (Exception ex) {
            log.error("Get invoices by current user failed, cause={}", ex.getMessage());
            response = DataResponse.failResponse(ex.getMessage());
        }
        return response;
    }

    /**
     * Cập nhật thông tin hóa đơn.
     * @param id ID của hóa đơn cần cập nhật
     * @param request Yêu cầu cập nhật hóa đơn
     * @return Thông tin hóa đơn đã cập nhật
     */
    @PutMapping("/invoice/{id}")
    public DataResponse<InvoiceResponse> updateInvoice(@PathVariable String id, @RequestBody InvoiceRequest request) {
        DataResponse<InvoiceResponse> response;
        try {
            InvoiceResponse invoice = invoiceService.updateInvoice(id, request);
            response = DataResponse.successResponse(invoice, "Cập nhật hóa đơn thành công");
        } catch (Exception ex) {
            log.error("Update invoice failed, cause={}", ex.getMessage());
            response = DataResponse.failResponse(ex.getMessage());
        }
        return response;
    }

    /**
     * Xóa hóa đơn theo ID.
     * @param id ID của hóa đơn
     * @return Kết quả xóa hóa đơn
     */
    @DeleteMapping("/invoice/{id}")
    public DataResponse<Void> deleteInvoice(@PathVariable String id) {
        DataResponse<Void> response;
        try {
            invoiceService.deleteInvoice(id);
            response = DataResponse.successResponse("Xóa hóa đơn thành công");
        } catch (Exception ex) {
            log.error("Delete invoice failed, cause={}", ex.getMessage());
            response = DataResponse.failResponse(ex.getMessage());
        }
        return response;
    }

}
