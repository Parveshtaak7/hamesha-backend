package com.humesha.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.humesha.demo.model.StoreStatus;
import com.humesha.demo.repository.StoreStatusRepository;

@RestController
@RequestMapping("/api/store")
@CrossOrigin(origins = {"https://humesha-restaurant.vercel.app", "http://localhost:5173", "http://localhost:5174", "http://frontend-owner-chi.vercel.app"})
public class StoreStatusController {

    @Autowired
    private StoreStatusRepository storeStatusRepository;

    // Simple custom object to bypass all Java generic mapping errors
    public static class StatusResponse {
        public boolean isOpen;
        public StatusResponse(boolean isOpen) {
            this.isOpen = isOpen;
        }
    }

    @GetMapping("/status")
    public ResponseEntity<StatusResponse> getStatus() {
        StoreStatus status = storeStatusRepository.findById(1L).orElseGet(() -> {
            StoreStatus newStatus = new StoreStatus();
            newStatus.setId(1L);
            newStatus.setOpen(true);
            return storeStatusRepository.save(newStatus);
        });
        
        return ResponseEntity.ok(new StatusResponse(status.isOpen()));
    }

    @PutMapping("/status/toggle")
    public ResponseEntity<StatusResponse> toggleStatus() {
        StoreStatus status = storeStatusRepository.findById(1L).orElseGet(() -> {
            StoreStatus newStatus = new StoreStatus();
            newStatus.setId(1L);
            newStatus.setOpen(true);
            return storeStatusRepository.save(newStatus);
        });
        
        status.setOpen(!status.isOpen());
        storeStatusRepository.save(status);
        
        return ResponseEntity.ok(new StatusResponse(status.isOpen()));
    }
}