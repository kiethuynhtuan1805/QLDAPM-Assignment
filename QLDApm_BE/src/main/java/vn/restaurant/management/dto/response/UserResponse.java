package vn.restaurant.management.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.restaurant.management.entity.User;

import java.util.Date;


@Getter
@Setter
@Builder
@JsonIgnoreProperties
public class UserResponse {
    private String userId;
    private String roleId;
    private String nameRole;
    private String firstname;
    private String lastname;
    private Boolean gender;
    private Date dateOfBirth;
    private String address;
    private String email;
    private String phone;
    private String avatar;
    private String username;
    private Date dateCreated;
    private Date dateUpdated;

    public static UserResponse fromUser(User user, String roleName) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .roleId(user.getRoleId())
                .nameRole(roleName)
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .gender(user.getGender())
                .dateOfBirth(user.getDateOfBirth())
                .address(user.getAddress())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatar(user.getAvatar())
                .username(user.getUsername())
                .dateCreated(user.getDateCreated())
                .dateUpdated(user.getDateUpdated())
                .build();
    }
}
