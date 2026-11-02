package com.humesha.demo.dto;

import com.humesha.demo.model.User;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String jwt;
    private User user;
}