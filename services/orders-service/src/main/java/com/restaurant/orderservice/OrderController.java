package com.restaurant.orderservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * Контролер для обробки HTTP-запитів, пов'язаних із замовленнями.
 * Анотація @RestController поєднує в собі @Controller та @ResponseBody,
 * що означає, що методи цього класу повертатимуть дані (наприклад, JSON) напряму в тілі відповіді.
 *
 * Анотація @RequestMapping("/orders") вказує, що всі шляхи в цьому контролері
 * будуть починатися з префікса /orders.
 */
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    /**
     * Конструктор з ін'єкцією залежностей (Dependency Injection).
     * Spring автоматично підставить сюди екземпляр OrderService.
     * @param orderService сервіс для роботи з замовленнями.
     */
    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Отримує список всіх замовлень.
     * Обробляє запити GET /orders.
     * @return список всіх замовлень.
     */
    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.findAll();
    }

    /**
     * Отримує конкретну страву за її ID.
     * Обробляє запити GET /orders/dishes/{id}.
     * @param id ідентифікатор страви, переданий в URL.
     * @return ResponseEntity, що містить страву та статус 200 OK, або статус 404 Not Found.
     */
    @GetMapping("/dishes/{id}")
    public ResponseEntity<Order> getDishById(@PathVariable Integer id) {
        Optional<Order> dish = orderService.findDishById(id);
        return dish.map(ResponseEntity::ok)
                   .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
