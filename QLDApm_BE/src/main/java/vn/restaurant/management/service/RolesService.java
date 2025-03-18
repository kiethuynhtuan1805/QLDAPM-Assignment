package vn.restaurant.management.service;

import vn.restaurant.management.dto.request.RolesRequest;
import vn.restaurant.management.entity.Roles;

import java.util.List;

public interface RolesService {

    List<Roles> getAllRoles();

    Roles getRoleById(String id);

    Roles addRole(RolesRequest roles);

    Roles updateRole(String id, RolesRequest roles);

    void deleteRole(String id);
}
