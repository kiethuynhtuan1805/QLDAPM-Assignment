package vn.restaurant.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.restaurant.management.entity.Images;

import java.util.List;

public interface ImagesRepository extends JpaRepository<Images, String> {
    List<Images> findByDishId(String dishId);
}
