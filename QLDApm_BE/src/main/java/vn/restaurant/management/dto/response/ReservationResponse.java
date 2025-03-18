package vn.restaurant.management.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import vn.restaurant.management.entity.Orders;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@JsonIgnoreProperties
public class ReservationResponse implements Serializable {
    private String reservationId;
    private String userId;
    private String username;
    private String tableId;
    private String tableName;
    private String area;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Ho_Chi_Minh")
    private Date reservationTime;
    private Integer numberOfGuests;
    private String specialRequests;
    private String status;
    private Orders orders;
}
