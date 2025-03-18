package vn.restaurant.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.restaurant.management.dto.request.UserRequest;
import vn.restaurant.management.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Boolean existsByEmail(String email);
    Boolean existsByPhone(String phone);
    Boolean existsByUsername(String username);
    User findByEmail(String email);
    User findByPhone(String phone);
    Optional<User> findByUsername(String username);
}
