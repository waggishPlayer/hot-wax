package com.hotwax.repository;

import com.hotwax.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    List<OrderItem> findByOrderId(Integer orderId);
    Optional<OrderItem> findByOrderItemSeqIdAndOrderId(Integer orderItemSeqId, Integer orderId);
    void deleteByOrderItemSeqIdAndOrderId(Integer orderItemSeqId, Integer orderId);
}
