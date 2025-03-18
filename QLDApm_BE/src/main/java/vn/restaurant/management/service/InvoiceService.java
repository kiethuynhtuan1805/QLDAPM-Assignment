package vn.restaurant.management.service;

import vn.restaurant.management.dto.request.InvoiceRequest;
import vn.restaurant.management.dto.response.InvoiceResponse;

import java.util.List;

public interface InvoiceService {
    InvoiceResponse updateInvoice(String id, InvoiceRequest request);

    void deleteInvoice(String id);

    InvoiceResponse getById(String id);

    List<InvoiceResponse> getAll();

    List<InvoiceResponse> getInvoicesByCurrentUser();
}
