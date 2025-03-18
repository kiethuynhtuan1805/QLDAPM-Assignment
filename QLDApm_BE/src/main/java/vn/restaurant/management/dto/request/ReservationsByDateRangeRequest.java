package vn.restaurant.management.dto.request;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationsByDateRangeRequest {
    private String startDate;
    private String endDate;
}
