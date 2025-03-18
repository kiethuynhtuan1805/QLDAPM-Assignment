package vn.restaurant.management.service;

import vn.restaurant.management.dto.request.CategoryRequest;
import vn.restaurant.management.entity.Category;
import vn.restaurant.management.exception.category.CategoryNotFoundException;

import java.util.List;

public interface CategoryService {

    List<Category> getFindAllCategories() throws CategoryNotFoundException;

    Category getFindByIdCategory(String id) throws CategoryNotFoundException;

    Category create(CategoryRequest request);

    Category update(String id, CategoryRequest request) throws CategoryNotFoundException;

    void delete(String id) throws CategoryNotFoundException;
}
