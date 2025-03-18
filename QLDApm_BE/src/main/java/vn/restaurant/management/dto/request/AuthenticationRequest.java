package vn.restaurant.management.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
@JsonIgnoreProperties
public class AuthenticationRequest {
    private String username;
    private String password;
}
