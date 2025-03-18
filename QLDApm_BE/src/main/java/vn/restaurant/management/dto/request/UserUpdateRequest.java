package vn.restaurant.management.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Getter
@Setter
@Builder
@JsonIgnoreProperties
public class UserUpdateRequest {
    private String roleId;
    private String nameRole;
    private String firstname;
    private String lastname;
    private Boolean gender;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateOfBirth;
    private String address;
    private String email;
    private String phone;
    private MultipartFile avatar;
    private String username;
    private Date dateUpdated;
}
