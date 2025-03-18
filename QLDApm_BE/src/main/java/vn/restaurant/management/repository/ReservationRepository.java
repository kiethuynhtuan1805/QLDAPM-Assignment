package vn.restaurant.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.restaurant.management.entity.Reservation;

import java.util.Date;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, String> {
    // Phương thức tìm kiếm các đặt chỗ trong một khoảng thời gian cụ thể
    @Query("SELECT r FROM Reservation r WHERE r.reservationTime >= :startDate AND r.reservationTime <= :endDate")
    List<Reservation> findByReservationTimeBetween(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);
    List<Reservation> findByTableId(String tableId);
}
