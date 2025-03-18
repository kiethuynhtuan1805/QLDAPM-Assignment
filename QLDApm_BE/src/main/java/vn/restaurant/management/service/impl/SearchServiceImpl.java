package vn.restaurant.management.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.restaurant.management.dto.response.DishResponse;
import vn.restaurant.management.entity.Category;
import vn.restaurant.management.entity.Dish;
import vn.restaurant.management.entity.Images;
import vn.restaurant.management.exception.dish.DishNotFoundException;
import vn.restaurant.management.repository.CategoryRepository;
import vn.restaurant.management.repository.ImagesRepository;
import vn.restaurant.management.repository.SearchRepository;
import vn.restaurant.management.service.CategoryService;
import vn.restaurant.management.service.SearchService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    private SearchRepository searchRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ImagesRepository imagesRepository;

    @Override
    public List<DishResponse> searchDish(String key) throws DishNotFoundException {
        // Lấy map categoryId -> categoryName
        Map<String, String> categories = getCategoryMap();

        // Tìm danh sách món ăn với từ khóa tìm kiếm
        List<Dish> dishes = searchRepository.findSearchDishes(key);
        if (dishes.isEmpty()) {
            throw new DishNotFoundException("Không tìm thấy món ăn với từ khóa tìm kiếm");
        }

        // Chuyển đổi từ Dish sang DishResponse
        return dishes.stream()
                .map(dish -> DishResponse.builder()
                        .dishId(dish.getDishId())
                        .categoryId(dish.getCategoryId())
                        .categoryName(categories.get(dish.getCategoryId()))
                        .dishName(dish.getName())
                        .description(dish.getDescription())
                        .price(dish.getPrice())
                        .image(dish.getImage())
                        .available(dish.isAvailable())
                        .rating(dish.getRating())
                        .classify(dish.getClassify())
                        .subImages(getSubImagesByDishId(dish.getDishId())) // Lấy hình ảnh liên quan
                        .build())
                .collect(Collectors.toList());
    }

    private List<Images> getSubImagesByDishId(String dishId) {
        return imagesRepository.findByDishId(dishId);
    }

    private Map<String, String> getCategoryMap() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .collect(Collectors.toMap(Category::getCategoryId, Category::getName));
    }
}
