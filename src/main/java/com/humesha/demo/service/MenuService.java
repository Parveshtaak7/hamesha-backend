package com.humesha.demo.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.humesha.demo.model.MenuItem;
import com.humesha.demo.repository.MenuItemRepository;

@Service
public class MenuService {
    
    private final MenuItemRepository menuItemRepository;

    public MenuService(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    public List<MenuItem> getAllAvailableItems() {
        return menuItemRepository.findAll();
    }

    // Automatically groups your items by category for the React frontend
    public Map<String, List<MenuItem>> getItemsGroupedByCategory() {
        List<MenuItem> availableItems = getAllAvailableItems();
        return availableItems.stream()
                .collect(Collectors.groupingBy(MenuItem::getCategory));
    }

    public MenuItem saveMenuItem(MenuItem item) {
        return menuItemRepository.save(item);
    }
}