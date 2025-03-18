package vn.restaurant.management.service;

import org.springframework.web.multipart.MultipartFile;
import vn.restaurant.management.dto.request.ChangePasswordResquest;
import vn.restaurant.management.dto.request.UserRequest;
import vn.restaurant.management.dto.request.UserUpdateRequest;
import vn.restaurant.management.dto.response.UserResponse;
import vn.restaurant.management.entity.User;
import vn.restaurant.management.exception.user.UserNotFoundException;

import java.io.IOException;
import java.util.List;
import java.util.Map;


public interface UserService {

    /**
     * Hiểm thị tất cả User
     */
    List<UserResponse> getUserAll();

    /**
     * Hiểm thị thông tin user theo mã id của nhân viên
     * @param id
     * @return
     * @throws UserNotFoundException
     */
    UserResponse getUserById(String id) throws UserNotFoundException;

    /**
     * Hiểm thị thông tin user theo email
     * @param email
     * @return
     * @throws UserNotFoundException
     */
    UserResponse getUserByEmail(String email) throws UserNotFoundException;

    /**
     * Hiểm thị thông tin user theo phone
     * @param phone
     * @return
     * @throws UserNotFoundException
     */
    UserResponse getUserByPhone(String phone) throws UserNotFoundException;

    /**
     * Tạo tài khoản user
     * @param userRequest
     * @return User
     */
    User createUser(UserRequest userRequest);

    /**
     * Cập nhật thông user
     * @param id
     * @param userRequest
     * @return
     * @throws UserNotFoundException
     */
    User updateUser(String id, UserUpdateRequest userRequest) throws UserNotFoundException;

    /**
     * Xóa user theo id
     * @param id
     * @throws UserNotFoundException
     */
    void deleteUserByid(String id) throws UserNotFoundException;

    /**
     * cập nhật avatar
     * @param id mã user
     * @param avatar = file ảnh
     * @return
     * @throws UserNotFoundException
     */
    User updateAvatarUser(String id, MultipartFile avatar) throws UserNotFoundException;

    /**
     * cập nhật mật khẩu
     * @param id
     * @throws UserNotFoundException
     */
    void updatePassword(String id, ChangePasswordResquest changePasswordResquest) throws UserNotFoundException;

    /**
     * Kiểm tra username có đã tồn tại chưa
     * @param username
     * @return
     */
    Boolean isUserByUsername(String username);

    /**
     * Kiểm tra Email đã tồn tại chưa
     * @param email
     * @return
     */
    Boolean isUserByEmail(String email);

    /**
     * Kiểm tra phone đã tồn tại chưa
     * @param phoneNumber
     * @return
     */
    Boolean isUserByPhoneNumber(String phoneNumber);

    /**
     * Duyệt qua mảng để lấy id và name role
     * @return
     */
    Map<String, String> getRoleMap();

    UserResponse getMyInfo() throws UserNotFoundException;
}
