package vn.restaurant.management.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@JsonIgnoreProperties
public class TablesRequest implements Serializable {

    private String name;

    private Integer capacity;

    private String area;

    private Boolean status;

}
