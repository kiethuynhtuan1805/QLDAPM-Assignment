package vn.restaurant.management;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import vn.restaurant.management.entity.Roles;
import vn.restaurant.management.entity.User;
import vn.restaurant.management.repository.RolesRepository;
import vn.restaurant.management.repository.UserRepository;

import java.util.Date;
import java.util.UUID;

@SpringBootTest
class RestaurantManagementApplicationTests {

    @Autowired
    RolesRepository rolesRepository;

    @Autowired
    UserRepository userRepository;


    @Test
    void TestRole() {
        rolesRepository.save(Roles.builder()
                        .roleId(UUID.randomUUID().toString().substring(0,7))
                        .name("ROLE_ADMIN")
                        .build());
        Assertions.assertNotNull(rolesRepository.findAll());
    }
}
