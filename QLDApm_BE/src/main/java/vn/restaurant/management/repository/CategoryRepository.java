package vn.restaurant.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.restaurant.management.entity.Category;
import vn.restaurant.management.entity.Dish;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {

}
