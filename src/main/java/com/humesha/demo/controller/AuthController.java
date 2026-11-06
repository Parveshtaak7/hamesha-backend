package com.humesha.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.humesha.demo.model.User;
import com.humesha.demo.repository.UserRepository;
import com.humesha.demo.util.JwtUtil;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"https://humesha-restaurant.vercel.app", "http://localhost:5173", "http://localhost:5174"})
public class AuthController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtUtil jwtUtil; // Added our new JWT Utility

    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> payload) {
        String accessToken = payload.get("token");

        try {
           // 1. Setup the request
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

            // 2. Fetch from Google (Fixed raw type warning)
            ResponseEntity<?> googleResponse = restTemplate.exchange(
                    "https://www.googleapis.com/oauth2/v3/userinfo",
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            // 3. Extract body and explicitly cast it to Map<String, Object> (Fixed Type mismatch error)
            @SuppressWarnings("unchecked")
            Map<String, Object> userInfo = (Map<String, Object>) googleResponse.getBody();
            
            String email = (String) userInfo.get("email");
            String name = (String) userInfo.get("name");

            User user = userRepository.findByEmail(email).orElse(null);
            
            if (user == null) {
                user = new User();
                user.setEmail(email);
                user.setName(name);
                user.setRole("ROLE_CUSTOMER"); // Ensure new users get standard customer role
                user = userRepository.save(user);
            }

            // GENERATE SECURE JWT INSTEAD OF VULNERABLE ID STRING
            String secureToken = jwtUtil.generateToken(user);

            Map<String, String> response = new HashMap<>();
            response.put("token", secureToken);
            response.put("name", user.getName()); 
            response.put("role", user.getRole()); // Sends role so the React frontend knows if they are an admin

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Google token");
        }
    }
}