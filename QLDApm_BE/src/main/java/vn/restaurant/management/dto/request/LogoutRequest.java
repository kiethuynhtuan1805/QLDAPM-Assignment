package vn.restaurant.management.dto.request;

import lombok.*;

import java.io.Serializable;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LogoutRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String accessToken;
}
