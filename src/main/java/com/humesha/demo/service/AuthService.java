package com.humesha.demo.service;

import java.util.Collections;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.humesha.demo.model.User;
import com.humesha.demo.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final GoogleIdTokenVerifier verifier;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList("144343722591-b5m9313lv8polgp2127kd7itl0qltgg5.apps.googleusercontent.com")) 
                .build();
    }

    public User verifyGoogleTokenAndLogin(String credential) throws Exception {
        GoogleIdToken idToken = verifier.verify(credential);
        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String picture = (String) payload.get("picture"); // Grab Google profile pic

            Optional<User> existingUser = userRepository.findByEmail(email);
            if (existingUser.isPresent()) {
                User user = existingUser.get();
                // Update details in case they changed on Google
                user.setName(name);
                user.setPicture(picture);
                return userRepository.save(user);
            } else {
                User newUser = new User();
                newUser.setEmail(email);
                newUser.setName(name);
                newUser.setPicture(picture);
                newUser.setRole("CUSTOMER"); 
                return userRepository.save(newUser);
            }
        } else {
            throw new RuntimeException("Invalid ID token.");
        }
    }
}