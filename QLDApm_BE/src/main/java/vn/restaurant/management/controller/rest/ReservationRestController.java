package vn.restaurant.management.controller.rest;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.restaurant.management.constant.Common;
import vn.restaurant.management.dto.request.ReservationRequest;
import vn.restaurant.management.dto.request.ReservationsByDateRangeRequest;
import vn.restaurant.management.dto.response.DataResponse;
import vn.restaurant.management.dto.response.ReservationResponse;
import vn.restaurant.management.service.ReservationService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@CrossOrigin("*")
@RestController
@Slf4j
@RequestMapping(Common.API_PATH + Common.API_VERSION)
public class ReservationRestController {

    @Autowired
    private ReservationService reservationService;

    @GetMapping("/reservation")
    public DataResponse<List<ReservationResponse>> getAllReservations() {
        DataResponse<List<ReservationResponse>> response = new DataResponse<>();

        try{
            List<ReservationResponse> reservationResponse = reservationService.getAllReservations();
            response = DataResponse.successResponse(reservationResponse, "Danh sách tất cả các bàn");
        }catch (Exception e){
            log.error("Lỗi truy xuất danh sách {}" ,e.getMessage());
            response = DataResponse.failResponse("Không tìm thấy dữ liệu");
        }
        return response;
    }

    @GetMapping("/reservation/{id}")
    public DataResponse<ReservationResponse> getReservationById(@PathVariable String id) {
        DataResponse<ReservationResponse> response = new DataResponse<>();
        try{
            ReservationResponse reservation = reservationService.getReservationById(id);
            response = DataResponse.successResponse(reservation, "Dữ liệu đã được tìm thấy");
        }catch (Exception e){
            log.error("Lỗi truy xuất dữ liệu {}",e.getMessage());
            response = DataResponse.failResponse(e.getMessage());
        }
        return response;
    }

    @PostMapping("/reservation")
    public DataResponse<ReservationResponse> addReservation(@RequestBody ReservationRequest request) {
        DataResponse<ReservationResponse> response = new DataResponse<>();
        try {
            ReservationResponse reservation = reservationService.createReservation(request);
            response = DataResponse.successResponse(reservation, "Thêm thành công!");
        }catch (Exception e){
            log.error("Lỗi không thể thêm {}", e.getMessage());
            response = DataResponse.failResponse(e.getMessage());
        }
        return response;
    }

    @PutMapping("/reservation/{id}")
    public DataResponse<ReservationResponse> updateReservation(@PathVariable String id, @RequestBody ReservationRequest request) {
        DataResponse<ReservationResponse> response = new DataResponse<>();
        try{
            ReservationResponse reservation = reservationService.updateReservation(id, request);
            response = DataResponse.successResponse(reservation, "Cập nhật thành công!");
        }catch (Exception e){
            log.error("Lỗi không thể cập nhật {}", e.getMessage());
            response = DataResponse.failResponse("Lỗi không thể cập nhật dữ liệu");
        }
        return response;
    }

    @DeleteMapping("/reservation/{id}")
    public DataResponse<ReservationResponse> deleteReservation(@PathVariable String id) {
        DataResponse<ReservationResponse> response = new DataResponse<>();
        try{
            Boolean reservation = reservationService.deleteReservation(id);
            response = DataResponse.successResponse("Xóa thành công!");
        }catch (Exception e){
            log.error("Lỗi không thể xóa {}", e.getMessage());
            response = DataResponse.failResponse("Lỗi không thể xóa dữ liệu");
        }
        return response;
    }

    @PostMapping("/reservation/date-range")
    public DataResponse<List<ReservationResponse>> getReservationsByDateRange(@RequestBody ReservationsByDateRangeRequest request) {
        DataResponse<List<ReservationResponse>> response = new DataResponse<>();
        try {
            List<ReservationResponse> reservationResponses = reservationService.getReservationsByDate(request);
            response = DataResponse.successResponse(reservationResponses, "Danh sách đặt chỗ trong khoảng thời gian");
        } catch (Exception e) {
            log.error("Lỗi khi lấy danh sách đặt chỗ trong khoảng thời gian {}", e.getMessage());
            response = DataResponse.failResponse("Không thể truy xuất dữ liệu cho khoảng thời gian được chọn");
        }
        return response;
    }

    @GetMapping("/reservation/today")
    public DataResponse<List<ReservationResponse>> getTodayReservations() {
        DataResponse<List<ReservationResponse>> response;
        try {
            // Lấy ngày hiện tại
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String todayDate = dateFormat.format(new Date());

            // Xác định thời gian bắt đầu và kết thúc của ngày hiện tại
            String startDateTime = todayDate + " 00:00:00";
            String endDateTime = todayDate + " 23:59:59";

            // Định dạng ngày giờ với phạm vi chính xác
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date startDate = dateTimeFormat.parse(startDateTime);
            Date endDate = dateTimeFormat.parse(endDateTime);

            // Gọi service với phạm vi thời gian của ngày hiện tại
            List<ReservationResponse> reservationResponses = reservationService.getReservationsByDate(
                    new ReservationsByDateRangeRequest(dateTimeFormat.format(startDate), dateTimeFormat.format(endDate)));

            response = DataResponse.successResponse(reservationResponses, "Danh sách đặt chỗ trong ngày hiện tại");
        } catch (Exception e) {
            log.error("Lỗi khi lấy danh sách đặt chỗ cho ngày hiện tại {}", e.getMessage());
            response = DataResponse.failResponse("Không thể truy xuất dữ liệu cho ngày hôm nay");
        }
        return response;
    }

}
