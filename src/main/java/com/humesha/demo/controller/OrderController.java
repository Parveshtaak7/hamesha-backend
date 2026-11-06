package com.humesha.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.humesha.demo.dto.OrderRequest;
import com.humesha.demo.model.Order;
import com.humesha.demo.repository.OrderRepository;
import com.humesha.demo.service.OrderService;
import com.humesha.demo.util.JwtUtil;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = {"https://humesha-restaurant.vercel.app", "https://frontend-owner-chi.vercel.app", "http://localhost:5173", "http://localhost:5174",})
public class OrderController {

    private final OrderService orderService;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private JwtUtil jwtUtil;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // CUSTOMER CHECKOUT
    @PostMapping("/checkout")
    public ResponseEntity<?> placeOrder(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody OrderRequest orderRequest) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Please sign in before checkout.");
        }

        Long userId;
        try {
            String token = authHeader.substring(7);
            userId = jwtUtil.extractId(token);
            if (userId == null || userId <= 0) {
                return ResponseEntity.status(401).body("Invalid user session.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid or expired login session.");
        }

        try {
            Order savedOrder = orderService.placeOrder(userId, orderRequest);
            return ResponseEntity.ok(savedOrder);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // OWNER DASHBOARD - GET ACTIVE ORDERS
    @GetMapping("/active")
    public ResponseEntity<?> getActiveOrders(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        // Uses the new custom query to catch null statuses
        List<Order> activeOrders = orderRepository.findActiveOrders();
        return ResponseEntity.ok(activeOrders);
    }

    // OWNER DASHBOARD - UPDATE STATUS
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long id, 
            @RequestBody Map<String, String> payload,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        
        order.setStatus(payload.get("status"));
        Order updatedOrder = orderRepository.save(order);
        
        return ResponseEntity.ok(updatedOrder);
    }
    // CUSTOMER - GET ORDER HISTORY
@GetMapping("/user")
public ResponseEntity<?> getUserOrderHistory(
        @RequestHeader(value = "Authorization", required = false) String authHeader) {
    
    try {
        String token = authHeader.substring(7);
        Long userId = jwtUtil.extractId(token);
        
        List<Order> userOrders = orderRepository.findByUserId(userId); 
        return ResponseEntity.ok(userOrders);
    } catch (Exception e) {
        return ResponseEntity.status(401).body("Invalid session.");
    }
}
}