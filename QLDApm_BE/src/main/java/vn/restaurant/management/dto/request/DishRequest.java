package vn.restaurant.management.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class DishRequest {
    String categoryId;
    String name;
    String description;
    BigDecimal price;
    MultipartFile image;
    boolean available;
    float rating;
    String classify;

}
