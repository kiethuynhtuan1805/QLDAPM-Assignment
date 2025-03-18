package vn.restaurant.management.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.restaurant.management.config.UserContext;
import vn.restaurant.management.dto.request.ChangePasswordResquest;
import vn.restaurant.management.dto.request.UserRequest;
import vn.restaurant.management.dto.request.UserUpdateRequest;
import vn.restaurant.management.dto.response.UserResponse;
import vn.restaurant.management.entity.Roles;
import vn.restaurant.management.entity.User;
import vn.restaurant.management.exception.user.UserNotFoundException;
import vn.restaurant.management.repository.RolesRepository;
import vn.restaurant.management.repository.UserRepository;
import vn.restaurant.management.service.UserService;
import vn.restaurant.management.utils.DateUtils;
import vn.restaurant.management.utils.GeneralUtils;
import vn.restaurant.management.utils.PasswordUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private S3FileUploadServiceImpl s3FileUploadServiceImpl;

    @Autowired
    private UserContext userContext;

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public List<UserResponse> getUserAll() {
        Map<String, String> roleMap = getRoleMap();

        return userRepository.findAll().stream()
                .map(user -> UserResponse.fromUser(user, roleMap.get(user.getRoleId())))
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF') or #id.equals(authentication.principal.Userid)")
    public UserResponse getUserById(String id) throws UserNotFoundException {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with id " + id));

        Map<String, String> roleMap = getRoleMap();
        return UserResponse.fromUser(user, roleMap.get(user.getRoleId()));
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public UserResponse getUserByEmail(String email) throws UserNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User not found with email " + email);
        }
        Map<String, String> roleMap = getRoleMap();
        return UserResponse.fromUser(user, roleMap.get(user.getRoleId()));
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public UserResponse getUserByPhone(String phone) throws UserNotFoundException {
        User user = userRepository.findByPhone(phone);
        if (user == null) {
            throw new UserNotFoundException("User not found with phone " + phone);
        }
        Map<String, String> roleMap = getRoleMap();
        return UserResponse.fromUser(user, roleMap.get(user.getRoleId()));
    }

    @Override
    public User createUser(UserRequest userRequest) {

        User user = User.builder()
                .userId(GeneralUtils.generateId())
                .roleId(userRequest.getRoleId())
                .firstname(userRequest.getFirstname())
                .lastname(userRequest.getLastname())
                .dateOfBirth(userRequest.getDateOfBirth())
                .email(userRequest.getEmail())
                .address(userRequest.getAddress())
                .phone(userRequest.getPhone())
                .avatar(null)
                .gender(userRequest.getGender())
                .username(userRequest.getUsername())
                .passwords(PasswordUtils.generatePassword(userRequest.getPasswords()))
                .dateCreated(DateUtils.getCurrentDateTime())
                .build();

        return userRepository.save(user);

    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'CUSTOMER')")
    public User updateUser(String id, UserUpdateRequest userRequest) throws UserNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + id));
        updateUserInfo(user, userRequest);
        return userRepository.save(user);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'CUSTOMER')")
    public User updateAvatarUser(String id, MultipartFile avatar) throws UserNotFoundException{
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + id));
        user.setAvatar(uploadAvatar(avatar));
        return userRepository.save(user);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN')")
    public void deleteUserByid(String id) throws UserNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + id));
        userRepository.delete(user);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'CUSTOMER')")
    public void updatePassword(String id, ChangePasswordResquest resquest) throws UserNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + id));
        if (resquest.getCurrentPassword() == null || resquest.getCurrentPassword().trim().isEmpty()){
            throw new UserNotFoundException("Password is empty");
        }

        if (resquest.getNewPassword() == null || resquest.getNewPassword().trim().isEmpty()){
            throw new UserNotFoundException("New password is empty");
        }

        if (resquest.getConfirmPassword() == null || resquest.getConfirmPassword().trim().isEmpty()){
            throw new UserNotFoundException("Confirm password is empty");
        }

        if (!PasswordUtils.checkPassword(resquest.getCurrentPassword(), user.getPasswords())){
            throw new UserNotFoundException("Current password is incorrect");
        }else{
            if (PasswordUtils.checkPassword(resquest.getNewPassword(), user.getPasswords())){
                throw new UserNotFoundException("The new password can't be the same as the old password!");
            }
            if (resquest.getNewPassword().length() <= 8 || resquest.getNewPassword().length() >= 32){
                throw new IllegalArgumentException("New password must be between 8 and 32 characters long!");
            }
            if (!resquest.getNewPassword().matches(".*[A-Z].*")) {
                throw new IllegalArgumentException("New password must have at least one uppercase character!");
            }

            if (!resquest.getNewPassword().matches(".*\\d.*")) {
                throw new IllegalArgumentException("New password must have at least one digit!");
            }
            if (!resquest.getNewPassword().equals(resquest.getConfirmPassword())){
                throw new UserNotFoundException("The confirm password is incorrect");
            }
            user.setPasswords(PasswordUtils.generatePassword(resquest.getNewPassword()));
            userRepository.save(user);
        }
    }

    @Override
    public Boolean isUserByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public Boolean isUserByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public Boolean isUserByPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhone(phoneNumber);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public UserResponse getMyInfo() throws UserNotFoundException {

        String userId = userContext.getUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + userId));

        Map<String, String> roleMap = getRoleMap();
        return UserResponse.fromUser(user, roleMap.get(user.getRoleId()));
    }

    public Map<String, String> getRoleMap() {
        List<Roles> roles = rolesRepository.findAll();
        return roles.stream()
                .collect(Collectors.toMap(Roles::getRoleId, Roles::getName));
    }

    private String uploadAvatar(MultipartFile avatar) {
        try {
            if (avatar != null && !avatar.isEmpty()) {
                return s3FileUploadServiceImpl.uploadFile(avatar);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload avatar", e);
        }
        return null;
    }

    private void updateUserInfo(User user, UserUpdateRequest userRequest) {
        if (userRequest.getRoleId() != null) {
            user.setRoleId(userRequest.getRoleId());
        }

        // Cập nhật firstname
        if (userRequest.getFirstname() != null) {
            user.setFirstname(userRequest.getFirstname());
        }

        // Cập nhật lastname
        if (userRequest.getLastname() != null) {
            user.setLastname(userRequest.getLastname());
        }

        // Cập nhật địa chỉ
        if (userRequest.getAddress() != null) {
            user.setAddress(userRequest.getAddress());
        }

        // Cập nhật giới tính
        if (userRequest.getGender() != null) {
            user.setGender(userRequest.getGender());
        }

        // Cập nhật email
        if (userRequest.getEmail() != null) {
            user.setEmail(userRequest.getEmail());
        }

        // Cập nhật số điện thoại
        if (userRequest.getPhone() != null) {
            user.setPhone(userRequest.getPhone());
        }

        // Cập nhật ngày sinh
        if (userRequest.getDateOfBirth() != null) {
            user.setDateOfBirth(userRequest.getDateOfBirth());
        }
    }
}
