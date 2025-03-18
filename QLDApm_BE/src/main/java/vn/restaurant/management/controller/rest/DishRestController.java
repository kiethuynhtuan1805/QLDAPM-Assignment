package vn.restaurant.management.controller.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.restaurant.management.constant.Common;
import vn.restaurant.management.dto.request.DishRequest;
import vn.restaurant.management.dto.response.DataResponse;
import vn.restaurant.management.dto.response.DishResponse;
import vn.restaurant.management.exception.dish.DishNotFoundException;
import vn.restaurant.management.service.DishService;
import vn.restaurant.management.utils.PrintUtils;

import java.math.BigDecimal;
import java.util.List;


@CrossOrigin("*")
@RestController
@Slf4j
@RequestMapping(Common.API_PATH + Common.API_VERSION)
public class DishRestController {
    @Autowired
    private DishService dishService;

    @GetMapping("/dishes")
    private DataResponse<List<DishResponse>> getDishes() {
        DataResponse<List<DishResponse>> response = new DataResponse<>();
        try {
            List<DishResponse> dishes = dishService.getAllDishes();
            response = DataResponse.successResponse(dishes,"Danh sách Dish");
        }catch (Exception ex) {
            log.error("Get all dish failed, cause={}", ex.getMessage());
            response = DataResponse.failResponse(ex.getMessage());
        }
        return response;
    }

    @GetMapping("/dish/{id}")
    private DataResponse<DishResponse> getDish(@PathVariable String id) {
        DataResponse<DishResponse> response = new DataResponse<>();
        try{
            DishResponse dish = dishService.getDishById(id);
            response = DataResponse.successResponse(dish,"");
        }catch (Exception ex) {
            log.error("Get dish failed, cause={}", ex.getMessage());
            response = DataResponse.failResponse(ex.getMessage());
        }
        return response;
    }

    @PostMapping(value = "/dish", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    private DataResponse<DishResponse> addDish(@ModelAttribute DishRequest request) {
        DataResponse<DishResponse> response = new DataResponse<>();
        try{
            validationDish(request);
            DishResponse dishResponse = dishService.saveDish(request);
            response = DataResponse.successResponse(dishResponse,"Thêm món ăn thành công!");
            PrintUtils.print(request);
        }catch (Exception ex) {
            log.error("Create Dish failed, cause={}", ex.getMessage());
            response = DataResponse.failResponse(ex.getMessage());
        }
        return response;
    }

    @PutMapping("/dish/{id}")
    private DataResponse<DishResponse> updateDish(@PathVariable String id, @ModelAttribute DishRequest request) {
        DataResponse<DishResponse> response = new DataResponse<>();
        try{
            PrintUtils.print(request);
            validationDish(request);
            DishResponse dishResponse = dishService.updateDish(id, request);
            response = DataResponse.successResponse(dishResponse,"");
        }catch (Exception ex) {
            log.error("Update Dish failed, cause={}", ex.getMessage());
            response = DataResponse.failResponse(ex.getMessage());
        }
        return response;
    }

    @DeleteMapping("/dish/{id}")
    private DataResponse<DishResponse> deleteDish(@PathVariable String id) {
        DataResponse<DishResponse> response = new DataResponse<>();
        try {
            PrintUtils.print(id);
            dishService.deleteDish(id);
            response = DataResponse.successResponse("Xóa Món ăn thành công!");
        }catch (Exception ex) {
            log.error("Delete Dish failed, cause={}", ex.getMessage());
            response = DataResponse.failResponse(ex.getMessage());
        }
        return response;
    }


    private void validationDish(DishRequest request) throws DishNotFoundException {
        // Kiểm tra categoryId
        if (!StringUtils.hasText(request.getCategoryId())) {
            throw new DishNotFoundException("CategoryId không hợp lệ");
        }

        // Kiểm tra name
        if (!StringUtils.hasText(request.getName())) {
            throw new DishNotFoundException("Tên món ăn không hợp lệ");
        }

        // Kiểm tra price
        if (request.getPrice() == null || request.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new DishNotFoundException("Giá không hợp lệ, phải lớn hơn hoặc bằng 0");
        }

        // Kiểm tra rating
        if (request.getRating() < 0 || request.getRating() > 5) {
            throw new DishNotFoundException("Đánh giá phải từ 0 đến 5");
        }

        // Kiểm tra classify (nếu có yêu cầu)
        if (request.getClassify() != null && request.getClassify().length() > 50) {
            throw new DishNotFoundException("Phân loại không được dài quá 50 ký tự");
        }
    }
}
