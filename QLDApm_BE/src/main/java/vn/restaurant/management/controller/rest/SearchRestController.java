package vn.restaurant.management.controller.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.restaurant.management.constant.Common;
import vn.restaurant.management.dto.response.DataResponse;
import vn.restaurant.management.dto.response.DishResponse;
import vn.restaurant.management.exception.dish.DishNotFoundException;
import vn.restaurant.management.service.SearchService;

import java.util.List;

@CrossOrigin("*")
@RestController
@Slf4j
@RequestMapping(Common.API_PATH + Common.API_VERSION)
public class SearchRestController {
    @Autowired
    private SearchService searchService;

    @PostMapping("/search/dishes/{key}")
    public DataResponse<List<DishResponse>> searchDishes(@PathVariable String key) {
        try {
            List<DishResponse> dishes = searchService.searchDish(key);
            return DataResponse.successResponse(dishes, "Danh sách các món ăn phù hợp với từ khóa tìm kiếm");
        } catch (DishNotFoundException ex) {
            return DataResponse.failResponse(ex.getMessage());
        }
    }
}
