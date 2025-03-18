package vn.restaurant.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import vn.restaurant.management.entity.Authentication;

@Repository
public interface AuthenticationRepository extends JpaRepository<Authentication, String> {
    void deleteByAccessToken(String accessToken);
}
