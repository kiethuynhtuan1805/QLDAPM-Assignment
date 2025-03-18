package vn.restaurant.management.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="Reservation")
public class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "reservation_id", length = 10)
    private String reservationId;

    @Column(name = "user_id", length = 10)
    private String userId;

    @Column(name = "table_id", length = 10)
    private String tableId;

    @Column(name = "reservation_time")
    private Date reservationTime;

    @Column(name = "number_of_guests")
    private Integer numberOfGuests;

    @Column(name = "special_requests", columnDefinition = "TEXT")
    private String specialRequests;

    @Column(name = "status", length = 100)
    private String status;

    @Column(name = "date_created", updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date dateCreated;

    @Column(name = "date_updated", insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Date dateUpdated;
}
