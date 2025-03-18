package vn.restaurant.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.restaurant.management.entity.Invoice;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, String> {

    Optional<Invoice> findByOrderId(String orderId);

    @Query("SELECT i FROM Invoice i WHERE i.orderId IN :orderIds")
    List<Invoice> findByOrderIds(@Param("orderIds") List<String> orderIds);
}
