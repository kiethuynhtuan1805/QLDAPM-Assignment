package vn.restaurant.management.controller.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import vn.restaurant.management.constant.Common;
import vn.restaurant.management.dto.request.CategoryRequest;
import vn.restaurant.management.dto.request.RolesRequest;
import vn.restaurant.management.dto.response.DataResponse;
import vn.restaurant.management.entity.Category;
import vn.restaurant.management.exception.category.CategoryNotFoundException;
import vn.restaurant.management.service.CategoryService;
import vn.restaurant.management.utils.PrintUtils;
import java.util.List;

@CrossOrigin("*")
@RestController
@Slf4j
@RequestMapping(Common.API_PATH + Common.API_VERSION)
public class CategoryRestController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/categories")
    private DataResponse<List<Category>> getCategories() throws CategoryNotFoundException {
        DataResponse<List<Category>> response = new DataResponse<>();
        try{

            List<Category> categories = categoryService.getFindAllCategories();
            response = DataResponse.successResponse(categories,"Danh sách categories");
        }catch (Exception ex){
            log.error("Get all categories failed, cause={}", ex.getMessage());
            response = DataResponse.failResponse(ex.getMessage());
        }
        return response;
    }

    @GetMapping("/category/{id}")
    private DataResponse<Category> getCategory(@PathVariable String id) throws CategoryNotFoundException {
        DataResponse<Category> response = new DataResponse<>();
        try{
            Category category = categoryService.getFindByIdCategory(id);
            response = DataResponse.successResponse(category,"Danh sách Category");
        }catch (Exception ex){
            log.error("Get all categories failed, cause={}", ex.getMessage());
            response = DataResponse.failResponse(ex.getMessage());
        }
        return response;
    }

    @PostMapping("/category")
    private DataResponse<Category> createCategory(@RequestBody CategoryRequest request) throws CategoryNotFoundException {
        DataResponse<Category> response = new DataResponse<>();
        try{
            PrintUtils.print(request);
            validateCategoryRequest(request);
            Category category = categoryService.create(request);
            response = DataResponse.successResponse(category,"Category created successfully");
        }catch (Exception ex){
            log.error("Create category failed, cause={}", ex.getMessage());
            response = DataResponse.failResponse(ex.getMessage());
        }
        return response;
    }

    @PutMapping("/category/{id}")
    private DataResponse<Category> updateCategory(@PathVariable String id, @RequestBody CategoryRequest request) throws CategoryNotFoundException {
        DataResponse<Category> response = new DataResponse<>();
        try{
            PrintUtils.print(request);
            validateCategoryRequest(request);
            Category category = categoryService.update(id, request);
            response = DataResponse.successResponse(category,"Category updated successfully");
        }catch (Exception ex){
            log.error("Update category failed, cause={}", ex.getMessage());
            response = DataResponse.failResponse(ex.getMessage());
        }
        return response;
    }

    @DeleteMapping("/category/{id}")
    private DataResponse<Category> deleteCategory(@PathVariable String id) throws CategoryNotFoundException {
        DataResponse<Category> response = new DataResponse<>();
        try {
            categoryService.delete(id);
            response = DataResponse.successResponse("Category deleted successfully");
        }catch (Exception ex){
            log.error("Delete category failed, cause={}", ex.getMessage());
            response = DataResponse.failResponse(ex.getMessage());
        }
        return response;
    }

    private void validateCategoryRequest(CategoryRequest request) {
        if(!StringUtils.hasText(request.getName())) {
            throw new IllegalArgumentException("Category name invalid");
        }
    }
}
