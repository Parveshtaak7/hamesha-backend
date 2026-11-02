
package com.humesha.demo.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.humesha.demo.dto.OrderItemRequest;
import com.humesha.demo.dto.OrderRequest;
import com.humesha.demo.model.MenuItem;
import com.humesha.demo.model.Order;
import com.humesha.demo.model.OrderItem;
import com.humesha.demo.model.User;
import com.humesha.demo.repository.MenuItemRepository;
import com.humesha.demo.repository.OrderRepository;
import com.humesha.demo.repository.UserRepository;

@Service
public class OrderService {

    public List<Order> getOrderHistory(Long userId) {

    if (!userRepository.existsById(userId)) {
        throw new IllegalArgumentException("Customer not found.");
    }

    return orderRepository.findByUserIdOrderByOrderDateDesc(userId);
}

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final MenuItemRepository menuItemRepository;

    public OrderService(
            OrderRepository orderRepository,
            UserRepository userRepository,
            MenuItemRepository menuItemRepository) {

        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.menuItemRepository = menuItemRepository;
    }

    @Transactional
    public Order placeOrder(Long userId, OrderRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalStateException("Customer not found."));

        if (user.getPhone() == null || user.getPhone().isBlank()
                || user.getAddress() == null || user.getAddress().isBlank()) {

            throw new IllegalStateException(
                    "Please complete your phone number and address in your profile first."
            );
        }

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Your cart is empty.");
        }

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequest requestedItem : request.getItems()) {

            if (requestedItem.getMenuItemId() == null
                    || requestedItem.getQuantity() == null
                    || requestedItem.getQuantity() <= 0) {

                throw new IllegalArgumentException(
                        "Invalid item or quantity."
                );
            }

            MenuItem menuItem = menuItemRepository
                    .findById(requestedItem.getMenuItemId())
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "A selected menu item no longer exists."
                            ));

            if (menuItem.getPrice() == null || menuItem.getPrice() < 0) {
                throw new IllegalArgumentException(
                        "Invalid price for " + menuItem.getName()
                );
            }

            OrderItem orderItem = new OrderItem();

            orderItem.setItemName(menuItem.getName());
            orderItem.setQuantity(requestedItem.getQuantity());
            orderItem.setPrice(menuItem.getPrice());

            orderItems.add(orderItem);

            BigDecimal itemTotal = BigDecimal
                    .valueOf(menuItem.getPrice())
                    .multiply(BigDecimal.valueOf(requestedItem.getQuantity()));

            total = total.add(itemTotal);
        }

        Order order = new Order();

        order.setUserId(user.getId());
        order.setCustomerName(user.getName());
        order.setPhone(user.getPhone());
        order.setAddress(user.getAddress());

        order.setItems(orderItems);
        order.setTotalAmount(total.doubleValue());

        order.setStatus("PREPARING");
        order.setOrderDate(LocalDateTime.now());

        return orderRepository.save(order);
    }
}
