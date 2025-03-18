package vn.restaurant.management.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import vn.restaurant.management.dto.request.CategoryRequest;
import vn.restaurant.management.entity.Category;
import vn.restaurant.management.exception.category.CategoryNotFoundException;
import vn.restaurant.management.repository.CategoryRepository;
import vn.restaurant.management.service.CategoryService;
import vn.restaurant.management.utils.DateUtils;
import vn.restaurant.management.utils.GeneralUtils;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryRepository categoryRepository;

    @Override
    public List<Category> getFindAllCategories() throws CategoryNotFoundException {
        List<Category> categories = categoryRepository.findAll();

        if(categories.isEmpty()){
            throw new CategoryNotFoundException("Không có dữ liệu trong danh sách");
        }
        return categories;
    }

    @Override
    public Category getFindByIdCategory(String id) throws CategoryNotFoundException {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Không tìm thấy id: " + id));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Category create(CategoryRequest request) {
        Category category = Category.builder()
                .categoryId(GeneralUtils.generateId())
                .name(request.getName())
                .dateCreated(DateUtils.getCurrentDateTime())
                .build();
        return categoryRepository.save(category);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Category update(String id, CategoryRequest request) throws CategoryNotFoundException {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Không tìm thấy id: "+id));
        category.setName(request.getName());
        category.setDateUpdated(DateUtils.getCurrentDateTime());
        return categoryRepository.save(category);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(String id) throws CategoryNotFoundException {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Không tìm thấy id: "+id));
        categoryRepository.delete(category);
    }
}
