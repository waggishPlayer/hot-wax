package com.hotwax.repository;

import com.hotwax.model.OrderHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderHeaderRepository extends JpaRepository<OrderHeader, Long> {
    List<OrderHeader> findByCustomerId(Long customerId);
    List<OrderHeader> findByStatus(String status);
}
