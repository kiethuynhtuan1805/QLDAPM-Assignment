package vn.restaurant.management.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshTokenRequest implements Serializable {
    private String accessToken;
    private String refreshToken;
}
