package com.humesha.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.humesha.demo.model.User;
import com.humesha.demo.repository.UserRepository;
import com.humesha.demo.util.JwtUtil;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "https://humesha-restaurant.vercel.app")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtUtil jwtUtil; // Injected the JWT utility to decode secure tokens

    // Fetches the saved details so the modal isn't empty when opened
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String authHeader) {
        try {
            // Extract the actual JWT token without the "Bearer " prefix
            String token = authHeader.substring(7);
            
            // Securely extract the user's email directly from the token signature
            String email = jwtUtil.extractEmail(token);
            User user = userRepository.findByEmail(email).orElse(null);
            
            if (user != null) {
                Map<String, String> profile = new HashMap<>();
                // Safe null checks guarantee the response never fails
                profile.put("name", user.getName() != null ? user.getName() : "");
                profile.put("phone", user.getPhone() != null ? user.getPhone() : "");
                profile.put("address", user.getAddress() != null ? user.getAddress() : "");
                return ResponseEntity.ok(profile);
            }
            return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid token"));
        }
    }

    // Saves the Name, Phone, and Address to the database
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @RequestHeader("Authorization") String authHeader, 
            @RequestBody Map<String, String> payload) {
        try {
            System.out.println("--- INCOMING SAVE REQUEST ---");
            
            String token = authHeader.substring(7);
            String email = jwtUtil.extractEmail(token);
            
            User user = userRepository.findByEmail(email).orElse(null);
            
            if (user != null) {
                user.setName(payload.get("name"));
                user.setPhone(payload.get("phone"));
                user.setAddress(payload.get("address"));
                userRepository.save(user);
                
                System.out.println("Successfully saved to database for user: " + email);
                return ResponseEntity.ok().body(Map.of("message", "Profile updated"));
            }
            
            System.out.println("User not found in database for email: " + email);
            return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
        } catch (Exception e) {
            System.out.println("Error saving profile: " + e.getMessage());
            return ResponseEntity.status(401).body(Map.of("error", "Invalid token or server error"));
        }
    }
}