package vn.restaurant.management.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties
public class UpdatePaymentRequest {
    private String paymentMethod;
    private String paymentStatus;
}
