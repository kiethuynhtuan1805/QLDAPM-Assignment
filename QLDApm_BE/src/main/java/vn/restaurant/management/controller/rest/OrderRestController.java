package vn.restaurant.management.controller.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.restaurant.management.constant.Common;
import vn.restaurant.management.dto.response.DataResponse;
import vn.restaurant.management.dto.response.OrderResponse;
import vn.restaurant.management.service.OrderService;

import java.util.List;

@CrossOrigin("*")
@RestController
@Slf4j
@RequestMapping(Common.API_PATH + Common.API_VERSION)
public class OrderRestController {

    @Autowired
    private OrderService orderService;

    /**
     * Lấy danh sách tất cả các đơn hàng.
     * 
     * @return Danh sách các đơn hàng
     */
    @GetMapping("/orders")
    public DataResponse<List<OrderResponse>> getAllOrders() {
        DataResponse<List<OrderResponse>> response;
        try {
            List<OrderResponse> data = orderService.getAllOrders();
            response = DataResponse.successResponse(data, "Danh sách order");
        } catch (Exception ex) {
            log.error("Get all orders failed, cause={}", ex.getMessage());
            response = DataResponse.failResponse(ex.getMessage());
        }
        return response;
    }

    /**
     * Lấy thông tin chi tiết đơn hàng theo ID.
     * 
     * @param id ID của đơn hàng
     * @return Thông tin chi tiết đơn hàng
     */
    @GetMapping("/order/{id}")
    public DataResponse<OrderResponse> getOrderById(@PathVariable String id) {
        DataResponse<OrderResponse> response;
        try {
            OrderResponse order = orderService.getOrderById(id);
            response = DataResponse.successResponse(order, "Thông tin chi tiết order");
        } catch (Exception ex) {
            log.error("Get order by id failed, cause={}", ex.getMessage());
            response = DataResponse.failResponse(ex.getMessage());
        }
        return response;
    }

    @GetMapping("/order/table/{id}")
    public DataResponse<List<OrderResponse>> getOrdersByTable(@PathVariable String id) {
        DataResponse<List<OrderResponse>> response;
        try {
            List<OrderResponse> data = orderService.getOrdersByTableId(id);
            response = DataResponse.successResponse(data, "Danh sách order theo table");
        } catch (Exception ex) {
            response = DataResponse.failResponse(ex.getMessage());
        }
        return response;
    }

    /**
     * Lấy danh sách đơn hàng của người dùng theo ID người dùng.
     * 
     * @param userId ID của người dùng
     * @return Danh sách đơn hàng của người dùng
     */
    @GetMapping("/orders/user/{userId}")
    public DataResponse<List<OrderResponse>> getOrdersByUserId(@PathVariable String userId) {
        DataResponse<List<OrderResponse>> response;
        try {
            List<OrderResponse> data = orderService.getOrdersByUserId(userId);
            response = DataResponse.successResponse(data, "Danh sách order của user");
        } catch (Exception e) {
            response = DataResponse.failResponse(e.getMessage());
        }
        return response;
    }

    @GetMapping("/order/user")
    public DataResponse<List<OrderResponse>> getOrdersByCurrentUser() {
        DataResponse<List<OrderResponse>> response;
        try {
            List<OrderResponse> data = orderService.getOrdersByCurrentUser();
            response = DataResponse.successResponse(data, "Danh sách order của user");
        } catch (Exception e) {
            response = DataResponse.failResponse(e.getMessage());
        }
        return response;
    }

    /**
     * Xóa đơn hàng theo ID.
     * 
     * @param id ID của đơn hàng
     * @return Kết quả xóa đơn hàng
     */
    @DeleteMapping("/order/{id}")
    public DataResponse<Void> deleteOrder(@PathVariable String id) {
        DataResponse<Void> response;
        try {
            orderService.deleteOrder(id);
            response = DataResponse.successResponse("Xóa đơn hàng thành công");
        } catch (Exception ex) {
            log.error("Delete order failed, cause={}", ex.getMessage());
            response = DataResponse.failResponse(ex.getMessage());
        }
        return response;
    }
}
