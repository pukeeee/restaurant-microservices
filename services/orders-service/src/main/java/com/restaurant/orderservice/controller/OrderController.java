package com.restaurant.orderservice.controller;

import com.restaurant.orderservice.dto.CreateOrderRequest;
import com.restaurant.orderservice.model.MenuItem;
import com.restaurant.orderservice.model.Order;
import com.restaurant.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/menu")
    public List<MenuItem> getMenu() {
        return orderService.findAllMenuItems();
    }

    @PostMapping("/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public Order createOrder(@RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request);
    }

    /**
     * Знаходить всі замовлення для конкретного користувача.
     * Обробляє запити GET /api/orders/user/{userId}.
     * @param userId ID користувача, переданий в URL.
     * @return список замовлень користувача.
     */
    @GetMapping("/orders/user/{userId}")
    public List<Order> getOrdersByUserId(@PathVariable Integer userId) {
        return orderService.findOrdersByUserId(userId);
    }
}
