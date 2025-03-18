package vn.restaurant.management.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class ChangePasswordResquest {
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;
}
