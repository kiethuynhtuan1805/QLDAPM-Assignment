package vn.restaurant.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.restaurant.management.entity.RevokedToken;

@Repository
public interface RevokedTokenRepository extends JpaRepository<RevokedToken, String> {
    Boolean existsByAccessToken(String token);
}
