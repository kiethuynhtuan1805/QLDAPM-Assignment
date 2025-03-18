package vn.restaurant.management.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties
public class TablesResponse {
    private String tableId;

    private String name;

    private Integer capacity;

    private String area;

    private Boolean status;
}
