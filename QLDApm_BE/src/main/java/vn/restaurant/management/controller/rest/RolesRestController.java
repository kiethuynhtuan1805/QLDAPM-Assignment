package vn.restaurant.management.controller.rest;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import vn.restaurant.management.constant.Common;
import vn.restaurant.management.dto.request.RolesRequest;
import vn.restaurant.management.entity.Roles;
import vn.restaurant.management.service.RolesService;
import vn.restaurant.management.dto.response.DataResponse;
import vn.restaurant.management.utils.PrintUtils;

import java.util.List;
import java.util.NoSuchElementException;

@CrossOrigin("*")
@RestController
@Slf4j
@RequestMapping(Common.API_PATH + Common.API_VERSION)
public class RolesRestController {

    @Autowired
    private RolesService rolesService;

    @GetMapping("/roles")
    public DataResponse<List<Roles>> getAllRoles() {
        DataResponse<List<Roles>> response;
        try {
            List<Roles> roles = rolesService.getAllRoles();
            if (roles == null || roles.isEmpty()) {
                response = DataResponse.failResponse("Roles has no data");
            } else{
                response = DataResponse.successResponse(roles,"");
            }
        } catch (Exception ex) {
            log.error("Get all roles failed, cause={}", ex.getMessage());
            response = DataResponse.failResponse(ex.getMessage());
        }
        PrintUtils.print(response);
        return response;
    }

    @GetMapping("/role/{id}")
    public DataResponse<Roles> getRoleById(@PathVariable("id") String id) {
        DataResponse<Roles> response = new DataResponse<>();
        try {
            Roles role = rolesService.getRoleById(id);
            response = DataResponse.successResponse(role,"");
        }catch (Exception ex) {
            log.error("Get role by id failed, cause={}", ex.getMessage());
            response = DataResponse.failResponse(ex.getMessage());
        }finally {
            PrintUtils.print(response);
        }
        return response;
    }

    @PostMapping("/role")
    public DataResponse<Roles> addRole(@RequestBody RolesRequest rolesRequest) {
        DataResponse<Roles> response = new DataResponse<>();
        try {
            PrintUtils.print(rolesRequest);
            validateRolesRequest(rolesRequest);
            Roles role = rolesService.addRole(rolesRequest);
            response = DataResponse.successResponse(role,"Roles added successfully");
        } catch (Exception ex) {
            log.error("Add role failed - roles={}, cause={}",
                    rolesRequest.getRoleName(),
                    ex.getMessage());
            response = DataResponse.failResponse(ex.getMessage());
        } finally {
            PrintUtils.print(response);
        }
        return response;
    }

    @PutMapping("/role/{id}")
    public DataResponse<Roles> updateRole(@NotNull @PathVariable("id") String id, @RequestBody RolesRequest rolesRequest) {
        DataResponse<Roles> response = new DataResponse<>();
        try {
            if (id == null || id.isEmpty()){
                throw new NoSuchElementException("Not found role with id");
            }
            PrintUtils.print(rolesRequest);
            Roles updateRole = rolesService.updateRole(id,rolesRequest);
            response = DataResponse.successResponse(updateRole,"Roles updated successfully");
        }catch (Exception ex){
            log.error("Update role failed, cause={}", ex.getMessage());
            response = DataResponse.failResponse(ex.getMessage());
        }finally{
            PrintUtils.print(response);
        }
        return response;
    }

    @DeleteMapping("/role/{id}")
    public DataResponse<Roles> deleteRole(@NotNull @PathVariable("id") String id) {
        DataResponse<Roles> response = new DataResponse<>();
        try{
            if (id == null || id.isEmpty()){
                throw new NoSuchElementException("Not found role with id");
            }
            rolesService.deleteRole(id);
            response = DataResponse.successResponse("Roles date successfully");
        }catch (Exception ex){
            log.error("Delete role failed, cause={}", ex.getMessage());
            response = DataResponse.failResponse(ex.getMessage());
        }finally{
            PrintUtils.print(response);
        }
        return response;
    }

    private void validateRolesRequest(RolesRequest request) {
        if(!StringUtils.hasText(request.getRoleName())) {
            throw new IllegalArgumentException("Roles name invalid");
        }
    }
}
