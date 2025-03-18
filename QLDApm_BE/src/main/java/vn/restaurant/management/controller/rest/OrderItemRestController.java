package vn.restaurant.management.controller.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.restaurant.management.constant.Common;
import vn.restaurant.management.dto.request.OrderItemRequest;
import vn.restaurant.management.dto.response.DataResponse;
import vn.restaurant.management.dto.response.OrderResponse;
import vn.restaurant.management.service.OrderItemService;

@CrossOrigin("*")
@RestController
@Slf4j
@RequestMapping(Common.API_PATH + Common.API_VERSION)
public class OrderItemRestController {

    @Autowired
    private OrderItemService orderItemService;

    @PostMapping("/order-item")
    public DataResponse<OrderResponse> addOrderItem(@RequestBody OrderItemRequest request) {
        DataResponse<OrderResponse> response;
        try {
            OrderResponse orderResponse = orderItemService.addOrderItem(request);
            response = DataResponse.successResponse(orderResponse, "Thêm món thành công");
            log.info("Thêm món thành công");
        }catch (Exception e) {
            log.error("Lỗi thêm dữ liệu order item: {}", e.getMessage());
            response = DataResponse.failResponse("Thêm món không thành công");
        }
        return response;
    }

    @PutMapping("/order-item/{orderItemId}")
    public DataResponse<OrderResponse> updateOrderItem(@PathVariable String orderItemId,
                                                       @RequestBody OrderItemRequest updatedRequest) {
        DataResponse<OrderResponse> response;
        try {
            OrderResponse orderResponse = orderItemService.updateOrderItem(orderItemId, updatedRequest);
            response = DataResponse.successResponse(orderResponse, "Cập nhật món thành công");
            log.info("Cập nhật món thành công cho OrderItem ID: {}", orderItemId);
        } catch (Exception e) {
            log.error("Lỗi cập nhật order item cho OrderItem ID {}: {}", orderItemId, e.getMessage());
            response = DataResponse.failResponse("Cập nhật món không thành công");
        }
        return response;
    }


    @GetMapping("/order-item/order/{orderId}")
    public DataResponse<OrderResponse> getOrderItemsByOrderId(@PathVariable String orderId) {
        DataResponse<OrderResponse> response;
        try {
            OrderResponse orderResponse = orderItemService.getOrderItemsByOrderId(orderId);
            response = DataResponse.successResponse(orderResponse, "Lấy danh sách món thành công");
            log.info("Lấy danh sách món thành công cho Order ID: {}", orderId);
        } catch (Exception e) {
            log.error("Lỗi lấy dữ liệu order items cho Order ID {}: {}", orderId, e.getMessage());
            response = DataResponse.failResponse("Không thể lấy danh sách món");
        }
        return response;
    }

    @DeleteMapping("/order-item/{orderItemId}")
    public DataResponse<OrderResponse> deleteOrderItem(@PathVariable String orderItemId) {
        DataResponse<OrderResponse> response;
        try {
            OrderResponse orderResponse = orderItemService.deleteOrderItem(orderItemId);
            response = DataResponse.successResponse(orderResponse, "Xóa món thành công");
            log.info("Xóa món thành công cho OrderItem ID: {}", orderItemId);
        } catch (Exception e) {
            log.error("Lỗi xóa order item cho OrderItem ID {}: {}", orderItemId, e.getMessage());
            response = DataResponse.failResponse("Xóa món không thành công");
        }
        return response;
    }
}
