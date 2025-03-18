package vn.restaurant.management.controller.rest;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.restaurant.management.constant.Common;
import vn.restaurant.management.dto.request.ChangePasswordResquest;
import vn.restaurant.management.dto.request.UserRequest;
import vn.restaurant.management.dto.request.UserUpdateRequest;
import vn.restaurant.management.dto.response.DataResponse;
import vn.restaurant.management.dto.response.UserResponse;
import vn.restaurant.management.entity.User;
import vn.restaurant.management.service.UserService;
import vn.restaurant.management.utils.PrintUtils;

import java.util.List;
import java.util.NoSuchElementException;

@CrossOrigin("*")
@RestController
@Slf4j
@RequestMapping(Common.API_PATH + Common.API_VERSION)
public class UserRestController {

    @Autowired
    private UserService userService;

    /**
     * Hiểm thị danh sách user
     * @return
     */
    @GetMapping("/users")
    private DataResponse<List<UserResponse>> getAllUsers() {
        DataResponse<List<UserResponse>> response;
        try{
            List<UserResponse> users = userService.getUserAll();
            if (users == null || users.isEmpty()) {
                response = DataResponse.failResponse("Users has no data");
            }else{
                response = DataResponse.successResponse(users, "");
            }
        }catch (Exception ex){
            log.error("Get all users failed, cause={}", ex.getMessage());
            response = DataResponse.failResponse(ex.getMessage());
        }
        return response;
    }

    /**
     * Hiểm thị user theo id được tìm thấy
     * @param id
     * @return
     */
    @GetMapping("/user/{id}")
    private DataResponse<UserResponse> getUserById(@NotNull @PathVariable String id) {
        DataResponse<UserResponse> response;
        try{
            UserResponse user = userService.getUserById(id);
            if (user == null) {
                response = DataResponse.failResponse("User not found");
            }else{
                response = DataResponse.successResponse(user, "");
            }
        }catch (Exception ex){
            log.error("Get user by id failed, cause={}", ex.getMessage());
            response = DataResponse.failResponse(ex.getMessage());
        }

        return response;
    }

    @GetMapping("/user/email/{email}")
    private DataResponse<UserResponse> getUserByEmail(@NotNull @PathVariable String email) {
        DataResponse<UserResponse> response;
        try{
            UserResponse user = userService.getUserByEmail(email);
            if (user == null) {
                response = DataResponse.failResponse("User not found");
            }else{
                response = DataResponse.successResponse(user, "");
            }
        }catch (Exception ex){
            log.error("Get user by email failed, cause={}", ex.getMessage());
            response = DataResponse.failResponse(ex.getMessage());
        }

        return response;
    }

    @GetMapping("/user/phone/{phone}")
    private DataResponse<UserResponse> getUserByPhone(@NotNull @PathVariable String phone) {
        DataResponse<UserResponse> response;
        try{
            UserResponse user = userService.getUserByPhone(phone);
            if (user == null) {
                response = DataResponse.failResponse("User not found");
            }else{
                response = DataResponse.successResponse(user, "");
            }
        }catch (Exception ex){
            log.error("Get user by email failed, cause={}", ex.getMessage());
            response = DataResponse.failResponse(ex.getMessage());
        }

        return response;
    }

    @PostMapping("/user")
    private DataResponse<UserResponse> createUser(@RequestBody UserRequest userRequest) {
        DataResponse<UserResponse> response;
        try{
            PrintUtils.print(userRequest);
            validateUser(userRequest);
            validateUsername(userRequest);
            validatePasswords(userRequest);
            User user = userService.createUser(userRequest);
            UserResponse userResponse =  UserResponse.fromUser(user, userService.getRoleMap().get(user.getRoleId()));
            response = DataResponse.successResponse(userResponse,"Create successfully");
        }catch (Exception ex){
            log.error("Create user failed, cause={}", ex.getMessage());
            response = DataResponse.failResponse(ex.getMessage());
        }
        return response;
    }

    @PutMapping("/user/{id}")
    private DataResponse<UserResponse> update(@NotNull @PathVariable String id, @RequestBody UserUpdateRequest userRequest){
        DataResponse<UserResponse> response;
        try{
            if (id == null || id.isEmpty()){
                throw new NoSuchElementException("Not found user with id");
            }
            PrintUtils.print(userRequest);
            validateUpdateUser(userRequest);
            User updateUser = userService.updateUser(id, userRequest);
            UserResponse userResponse = UserResponse.fromUser(updateUser, userService.getRoleMap().get(updateUser.getRoleId()));
            response = DataResponse.successResponse(userResponse, "User updated successfully");
        }catch (Exception ex){
            log.error("Update user failed, cause={}", ex.getMessage());
            response = DataResponse.failResponse(ex.getMessage());
        }
        return response;
    }

    @DeleteMapping("/user/{id}")
    private DataResponse<UserResponse> deleteUser(@NotNull @PathVariable("id") String id){
        DataResponse<UserResponse> response;
        try{
            if (id == null || id.isEmpty()){
                throw new NoSuchElementException("Not found user with id");
            }
            userService.deleteUserByid(id);
            response = DataResponse.successResponse("User delete successfully");
        }catch (Exception ex){
            log.error("Delete user failed, cause={}", ex.getMessage());
            response = DataResponse.failResponse(ex.getMessage());
        }
        return response;
    }

    @PutMapping("/user/avatar/{id}")
    private DataResponse<UserResponse> updateUserAvatar(@NotNull @PathVariable String id ,@RequestParam("avatar") MultipartFile avatar) {
        DataResponse<UserResponse> response = new DataResponse<>();
        try {
            User updateAvatar = userService.updateAvatarUser(id, avatar);
            UserResponse userResponse = UserResponse.fromUser(updateAvatar, userService.getRoleMap().get(updateAvatar.getRoleId()));
            response = DataResponse.successResponse(userResponse, "Updated avatar successfully");
        }catch (Exception ex){
            log.error("Update avarta failse, cause={}", ex.getMessage());
            response = DataResponse.failResponse(ex.getMessage());
        }
        return response;
    }

    @PutMapping("/user/account/change-password/{id}")
    private DataResponse<UserResponse> changePassword(@NotNull @PathVariable String id, @RequestBody ChangePasswordResquest resquest) {
        DataResponse<UserResponse> response = new DataResponse<>();
        try{
            userService.updatePassword(id, resquest);
            response = DataResponse.successResponse("Password changed successfully");
        }catch (Exception ex){
            log.error("Update password failse, cause={}", ex.getMessage());
            response = DataResponse.failResponse(ex.getMessage());
        }

        return response;
    }

    @GetMapping("/user/info")
    private DataResponse<UserResponse> getUserInfo() {
        DataResponse<UserResponse> response;
        try{
            UserResponse user = userService.getMyInfo();
            if (user == null) {
                response = DataResponse.failResponse("User not found");
            }else{
                response = DataResponse.successResponse(user, "");
            }
        }catch (Exception ex){
            log.error("Get user by id failed, cause={}", ex.getMessage());
            response = DataResponse.failResponse(ex.getMessage());
        }

        return response;
    }

    /**
     * Validate kiểm tra các trường hợp không được trùng username, email, phone
     * Kiểm tra đúng định dạng email
     * kiểm tra đúng định dạng phone
     * Kiểm tra passwords nhập vào tối thiểu 8 và tối đa 32
     * Kiểm tra trường FirtsName và LastName không được để trống
     */
    private void validateUser(UserRequest userRequest) {
        if(userRequest.getFirstname() == null || userRequest.getFirstname().trim().isEmpty()){
            throw new IllegalArgumentException("Không được bỏ trống firstname");
        }

        if(userRequest.getLastname() == null || userRequest.getLastname().trim().isEmpty()){
            throw new IllegalArgumentException("Không được bỏ trống lastname");
        }
        if((userRequest.getEmail() == null || userRequest.getEmail().trim().isEmpty()) &&
                (userRequest.getPhone() == null || userRequest.getPhone().trim().isEmpty())) {
            throw new IllegalArgumentException("Phải ít nhất email hoặc phone");
        }
        if(userRequest.getEmail() == null || userRequest.getEmail().trim().isEmpty()){
            validatePhone(userRequest);
        }
        if(userRequest.getPhone() == null || userRequest.getPhone().trim().isEmpty()){
            validateEmail(userRequest);
        }
    }

    /**
     * Kểm tra trường hợp password không được để trống
     * Password phả từ 8 đến 32 kí tự
     * Phải có ít nhất một chữ hoa
     * phải có ít nhất một chữ số
     * @param userRequest
     */
    private void validatePasswords(UserRequest userRequest) {
        String passwords = userRequest.getPasswords();

        // Kiểm tra trường hợp password không được bỏ trống
        if(passwords == null || passwords.trim().isEmpty()){
            throw new IllegalArgumentException("password không được để trống");
        }

        // Kiểm tra password có độ dài từ 8 đến 32 ký tự
        if (passwords.length() <= 8 || passwords.length() >= 32) {
            throw new IllegalArgumentException("Mật khẩu phải có độ dài từ 8 đến 32 ký tự!");
        }

        // Kiểm tra phải có ít nhất một kí tự viết hoa
        if (!passwords.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất một ký tự viết hoa!");
        }

        if (!passwords.matches(".*\\d.*")) {
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất một chữ số!");
        }
    }

    /**
     * Kiểm tra trường hợp username đã tồn tại chưa
     * Kiểm tra username không được để trống
     * @param userRequest
     */
    //Kiểm tra username
    private void validateUsername(UserRequest userRequest) {

        // Kiểm tra username không được để trống
        if (userRequest.getUsername() == null || userRequest.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username không được để trống!");
        }

        // Kiểm tra xem username có tồn tại không
        if (userService.isUserByUsername(userRequest.getUsername())) {
            throw new IllegalArgumentException("Username đã tồn tại, vui lòng chọn tên khác!");
        }
    }

    /**
     * Kiểm tra email có đúng định dạng hay không
     * kiểm tra email có tồn tại không
     * @param userRequest
     */
    private void validateEmail(UserRequest userRequest) {
        String email = userRequest.getEmail();

        //Kiểm tra email có tồn tạ không
        if(userService.isUserByEmail(email)){
            throw new IllegalArgumentException("Email đã tồn tại!");
        }

        // Kiểm tra định dạng email
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[a-zA-Z]{2,}$";
        if (!email.matches(emailRegex)) {
            throw new IllegalArgumentException("Định dạng email không hợp lệ!");
        }
    }

    /**
     * Kiểm tra phone có đúng định dạng không
     * Kiểm tra phone đã tồn tại chưa
     * @param userRequest
     */
    private void validatePhone(UserRequest userRequest){
        String phone = userRequest.getPhone();

        // Kiểm tra định dạng số điện thoại
        String phoneRegex = "^(\\+84|0)(9[0-9]|8[0-9]|7[0-9]|6[0-9]|5[0-9]|4[0-9]|3[0-9])[0-9]{7}$";
        if (!phone.matches(phoneRegex)) {
            throw new IllegalArgumentException("Định dạng số điện thoại không hợp lệ!");
        }

        // Kiểm tra số điện thoại đã được sử dụng chưa
        if(userService.isUserByPhoneNumber(phone)){
            throw new IllegalArgumentException("Số điện thoại đã tồn tại, vui lòng chọn số khác!");
        }
    }

    private void validateUpdateUser(UserUpdateRequest userRequest) {
        if(userRequest.getFirstname() == null || userRequest.getFirstname().trim().isEmpty()){
            throw new IllegalArgumentException("Không được bỏ trống firstname");
        }

        if(userRequest.getLastname() == null || userRequest.getLastname().trim().isEmpty()){
            throw new IllegalArgumentException("Không được bỏ trống lastname");
        }
        if((userRequest.getEmail() == null || userRequest.getEmail().trim().isEmpty()) &&
                (userRequest.getPhone() == null || userRequest.getPhone().trim().isEmpty())) {
            throw new IllegalArgumentException("Phải ít nhất email hoặc phone");
        }
    }

}
