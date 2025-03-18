package vn.restaurant.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.restaurant.management.entity.Tables;

import jakarta.persistence.Table;
import java.util.Date;
import java.util.List;

@Repository
public interface TableRepository extends JpaRepository<Tables, String> {

    @Query("SELECT t FROM Tables t WHERE t.status = true AND t.tableId NOT IN " +
            "(SELECT r.tableId FROM Reservation r WHERE r.reservationTime BETWEEN :startOfDay AND :endOfDay)")
    List<Tables> findAvailableTablesByDate(@Param("startOfDay") Date startOfDay, @Param("endOfDay") Date endOfDay);

}
