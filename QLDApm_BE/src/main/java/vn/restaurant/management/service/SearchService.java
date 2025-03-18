package vn.restaurant.management.service;

import vn.restaurant.management.dto.response.DishResponse;
import vn.restaurant.management.exception.dish.DishNotFoundException;

import java.util.List;

public interface SearchService {
    List<DishResponse> searchDish(String key) throws DishNotFoundException;
}
