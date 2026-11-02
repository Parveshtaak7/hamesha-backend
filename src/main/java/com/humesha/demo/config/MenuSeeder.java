package com.humesha.demo.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.humesha.demo.model.MenuItem;
import com.humesha.demo.repository.MenuItemRepository;

@Configuration
public class MenuSeeder {

    @Bean
    CommandLineRunner loadMenuData(MenuItemRepository repository) {
        return args -> {
            // Only seed if the database is currently empty
            if (repository.count() == 0) {
                List<MenuItem> menu = new ArrayList<>();

                // SIPPS
                menu.add(createItem("SIPPS", "Hot Toddy", "A warm, soothing drink with honey, lemon, and spices.", 290.0)); //[cite: 13]
                menu.add(createItem("SIPPS", "Ginger Ale", "Ginger-infused carbonated beverage, refreshing and zesty.", 290.0)); //[cite: 13]
                menu.add(createItem("SIPPS", "Seasonal Shrubs", "House-made fruit and herb syrups with soda.", 290.0)); //[cite: 13]
                menu.add(createItem("SIPPS", "Rose Milk", "Chilled milk with fragrant rose syrup.", 290.0)); //[cite: 13]
                menu.add(createItem("SIPPS", "Fresh Lime Soda", "Fresh lime with a sparkling finish.", 220.0)); //[cite: 13]

                // COFFEES
                menu.add(createItem("COFFEES", "Espresso", "A bold, beautifully balanced coffee.", 190.0)); //[cite: 14]
                menu.add(createItem("COFFEES", "Latte", "Smooth espresso with silky steamed milk.", 225.0)); //[cite: 14]
                menu.add(createItem("COFFEES", "Cappuccino", "Rich espresso with soft milk foam.", 225.0)); //[cite: 14]
                menu.add(createItem("COFFEES", "Americano", "Espresso finished with hot water.", 210.0)); //[cite: 14]
                menu.add(createItem("COFFEES", "Hamesha Signature", "Our house coffee, made with care.", 275.0)); //[cite: 14]

                // TEAS
                menu.add(createItem("TEAS", "Masala Chai", "Comforting tea with warming spices.", 180.0)); //[cite: 15]
                menu.add(createItem("TEAS", "Classic Green Tea", "Light, fresh, and soothing.", 180.0)); //[cite: 15]
                menu.add(createItem("TEAS", "Lemon Tea", "Bright lemon with a clean tea finish.", 190.0)); //[cite: 15]
                menu.add(createItem("TEAS", "Iced Tea", "Chilled tea with a refreshing finish.", 220.0)); //[cite: 15]

                // NIBBLES
                menu.add(createItem("NIBBLES", "Garlic Bread", "Toasted bread with garlic butter and herbs.", 220.0)); //[cite: 16]
                menu.add(createItem("NIBBLES", "Loaded Nachos", "Crunchy nachos with cheese and salsa.", 280.0)); //[cite: 16]
                menu.add(createItem("NIBBLES", "Classic Fries", "Golden, crisp, and perfectly seasoned.", 200.0)); //[cite: 16]
                menu.add(createItem("NIBBLES", "Paneer Tikka", "Spiced paneer, grilled until smoky.", 320.0)); //[cite: 16]

                // MAINS
                menu.add(createItem("MAINS", "Grilled Sandwich", "Toasted sandwich with a savoury filling.", 280.0)); //[cite: 17]
                menu.add(createItem("MAINS", "Classic Burger", "A hearty burger with crisp lettuce and sauce.", 320.0)); //[cite: 17]
                menu.add(createItem("MAINS", "Penne Pasta", "Penne tossed in a rich, creamy sauce.", 350.0)); //[cite: 17]
                menu.add(createItem("MAINS", "Garden Fresh Salad", "Fresh greens with a bright house dressing.", 280.0)); //[cite: 17]

                // SWEETS
                menu.add(createItem("SWEETS", "Chocolate Brownie", "A rich chocolate treat, best enjoyed slowly.", 240.0)); //[cite: 18]
                menu.add(createItem("SWEETS", "Red Velvet Cake", "Soft cake layered with cream cheese frosting.", 280.0)); //[cite: 18]
                menu.add(createItem("SWEETS", "Tiramisu", "Coffee-kissed layers with a delicate finish.", 300.0)); //[cite: 18]
                menu.add(createItem("SWEETS", "Cheesecake", "Creamy, smooth, and gently sweet.", 290.0)); //[cite: 18]

                repository.saveAll(menu);
                System.out.println("Full menu database seeded successfully!");
            }
        };
    }

    // Helper method to keep code clean
    private MenuItem createItem(String category, String name, String description, Double price) {
        MenuItem item = new MenuItem();
        item.setCategory(category);
        item.setName(name);
        item.setDescription(description);
        item.setPrice(price);
        return item;
    }
}