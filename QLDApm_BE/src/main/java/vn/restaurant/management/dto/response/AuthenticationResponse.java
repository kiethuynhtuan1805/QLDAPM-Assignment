package vn.restaurant.management.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties
public class AuthenticationResponse {
    String accessToken;
    String refreshToken;
}
