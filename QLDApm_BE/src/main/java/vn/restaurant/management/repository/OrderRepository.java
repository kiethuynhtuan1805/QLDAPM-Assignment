package vn.restaurant.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.restaurant.management.entity.Orders;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Orders, String> {

    @Query("SELECT o FROM Orders o WHERE o.userId = :userId AND o.status = :status")
    List<Orders> findByUserIdAndStatus(@Param("userId") String userId, @Param("status") String status);

    List<Orders> findByUserId(String userId);

    @Query("SELECT o FROM Orders o " +
            "JOIN Reservation r ON o.reservationId = r.reservationId " +
            "JOIN Tables t ON r.tableId = t.tableId " +
            "WHERE t.tableId = :tableId")
    List<Orders> findByTableId(@Param("tableId") String tableId);

    Orders findByReservationId(String reservationId);
}
