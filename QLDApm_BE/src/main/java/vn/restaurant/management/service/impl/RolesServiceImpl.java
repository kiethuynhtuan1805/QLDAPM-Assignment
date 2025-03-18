package vn.restaurant.management.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import vn.restaurant.management.dto.request.RolesRequest;
import vn.restaurant.management.entity.Roles;
import vn.restaurant.management.repository.RolesRepository;
import vn.restaurant.management.service.RolesService;
import vn.restaurant.management.utils.GeneralUtils;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class RolesServiceImpl implements RolesService {

    @Autowired
    private RolesRepository rolesRepository;

    public List<Roles> getAllRoles() {
        return rolesRepository.findAll();
    }

    @Override
    public Roles getRoleById(String id) {
        Optional<Roles> role = rolesRepository.findById(id);
        return role.orElseThrow(() -> new NoSuchElementException("Role not found with id: " + id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Roles addRole(RolesRequest roles) {
        Roles role = Roles.builder()
                .roleId(GeneralUtils.generateId())
                .name(roles.getRoleName())
                .build();
        return rolesRepository.save(role);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Roles updateRole(String id, RolesRequest roles) {
        Roles role = rolesRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Role not found with id: " + id) );
        role.setName(roles.getRoleName());
        return rolesRepository.save(role);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteRole(String id) {
        if(!rolesRepository.existsById(id)) {
            throw new NoSuchElementException("Role not found with id: " + id);
        }
        rolesRepository.deleteById(id);
    }

}
