package com.humesha.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.humesha.demo.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
    // For the customer website (order history)
    List<Order> findByUserIdOrderByOrderDateDesc(Long userId);
    
    // For the owner dashboard (live orders) - includes NULL statuses!
    @Query("SELECT o FROM Order o WHERE o.status IS NULL OR o.status != 'COMPLETED' ORDER BY o.orderDate DESC")
    List<Order> findActiveOrders();
}