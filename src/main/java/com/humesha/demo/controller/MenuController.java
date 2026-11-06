package com.humesha.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.humesha.demo.model.MenuItem;
import com.humesha.demo.repository.MenuItemRepository;

@RestController
@RequestMapping("/api/menu")
@CrossOrigin(origins = {"https://humesha-restaurant.vercel.app", "https://frontend-owner-chi.vercel.app", "http://localhost:5173", "http://localhost:5174",})
public class MenuController {

    @Autowired
    private MenuItemRepository menuRepository;

    // Fetch ONLY active items (ignores trashed items)
    @GetMapping
    public List<MenuItem> getMenu() {
        return menuRepository.findByIsTrashFalse();
    }

    // Fetch ONLY trashed items for the Trash Page
    @GetMapping("/trash")
    public List<MenuItem> getTrashedMenus() {
        return menuRepository.findByIsTrashTrue();
    }

    @PostMapping
    public MenuItem addMenuItem(@RequestBody MenuItem item) {
        return menuRepository.save(item);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MenuItem> updateMenuItem(@PathVariable Long id, @RequestBody MenuItem updatedData) {
        return menuRepository.findById(id).map(item -> {
            item.setName(updatedData.getName());
            item.setDescription(updatedData.getDescription());
            item.setPrice(updatedData.getPrice());
            if (updatedData.getCategory() != null) {
                item.setCategory(updatedData.getCategory());
            }
            return ResponseEntity.ok(menuRepository.save(item));
        }).orElse(ResponseEntity.notFound().build());
    }

    // Move item to trash (Soft Delete)
    @PutMapping("/{id}/trash")
    public ResponseEntity<MenuItem> moveToTrash(@PathVariable Long id) {
        return menuRepository.findById(id).map(item -> {
            item.setTrash(true);
            return ResponseEntity.ok(menuRepository.save(item));
        }).orElse(ResponseEntity.notFound().build());
    }

    // Restore item from trash
    @PutMapping("/{id}/restore")
    public ResponseEntity<MenuItem> restoreFromTrash(@PathVariable Long id) {
        return menuRepository.findById(id).map(item -> {
            item.setTrash(false);
            return ResponseEntity.ok(menuRepository.save(item));
        }).orElse(ResponseEntity.notFound().build());
    }

    // Permanent Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMenuItem(@PathVariable Long id) {
        return menuRepository.findById(id).map(item -> {
            menuRepository.delete(item);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }

    // Category Management Endpoint
    @PutMapping("/category/rename")
    public ResponseEntity<?> renameCategory(
            @RequestParam String oldName, 
            @RequestParam String newName) {
        
        menuRepository.updateCategoryAcrossAllItems(oldName, newName);
        return ResponseEntity.ok().build();
    }
}