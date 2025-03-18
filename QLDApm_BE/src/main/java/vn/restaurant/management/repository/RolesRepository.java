package vn.restaurant.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.restaurant.management.entity.Roles;

@Repository
public interface RolesRepository extends JpaRepository<Roles, String> {

}
