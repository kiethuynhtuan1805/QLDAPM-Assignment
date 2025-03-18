package vn.restaurant.management.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import vn.restaurant.management.entity.Images;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties
public class DishResponse {
    private String dishId;
    private String categoryId;
    private String categoryName;
    private String dishName;
    private String description;
    private BigDecimal price;
    private String image;
    private boolean available;
    private float rating;
    private String classify;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Images> subImages;

}
