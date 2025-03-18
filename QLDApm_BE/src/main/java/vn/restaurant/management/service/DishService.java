package vn.restaurant.management.service;

import org.springframework.web.multipart.MultipartFile;
import vn.restaurant.management.dto.request.DishRequest;
import vn.restaurant.management.dto.response.DishResponse;
import vn.restaurant.management.entity.Dish;
import vn.restaurant.management.exception.dish.DishNotFoundException;

import java.io.IOException;
import java.util.List;

public interface DishService {
    List<DishResponse> getAllDishes() throws DishNotFoundException;

    DishResponse getDishById(String id) throws DishNotFoundException;

    DishResponse saveDish(DishRequest request) throws IOException, DishNotFoundException;

    DishResponse updateDish(String id, DishRequest request) throws DishNotFoundException, IOException;

    void deleteDish(String id)  throws DishNotFoundException;
}
