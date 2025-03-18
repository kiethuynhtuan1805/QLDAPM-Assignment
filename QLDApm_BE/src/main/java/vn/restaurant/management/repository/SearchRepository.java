package vn.restaurant.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.restaurant.management.entity.Dish;
import vn.restaurant.management.entity.Tables;

import java.util.List;

@Repository
public interface SearchRepository extends JpaRepository<Dish, String> {

    @Query("SELECT d FROM Dish d " +
            "WHERE d.name LIKE CONCAT('%', :keyword, '%') " +
            "OR d.description LIKE CONCAT('%', :keyword, '%') " +
            "OR d.classify LIKE CONCAT('%', :keyword, '%') " +
            "OR CAST(d.price AS string) LIKE CONCAT('%', :keyword, '%') " +
            "OR CAST(d.rating AS string) LIKE CONCAT('%', :keyword, '%')")
    List<Dish> findSearchDishes(@Param("keyword") String keyword);

}
