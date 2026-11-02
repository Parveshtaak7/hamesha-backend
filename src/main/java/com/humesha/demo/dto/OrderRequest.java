package com.humesha.demo.dto;

import java.util.List;

import lombok.Data;

@Data
public class OrderRequest {
    private Long userId; // Included if the user is logged in
    private String customerName; 
    private Double totalDiscount = 0.0;
    private List<OrderItemRequest> items;
}