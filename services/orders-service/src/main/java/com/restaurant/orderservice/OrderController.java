package com.restaurant.orderservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Шар контролера (Controller Layer), також відомий як API Layer.
 * Відповідає за обробку вхідних HTTP-запитів.
 *
 * Анотація @RestController є комбінацією @Controller та @ResponseBody.
 * Вона вказує Spring, що цей клас є обробником веб-запитів, і результати його методів
 * мають бути автоматично серіалізовані в JSON та записані у тіло HTTP-відповіді.
 */
@RestController
public class OrderController {

    // Поле для зберігання залежності від сервісного шару.
    private final OrderService orderService;

    /**
     * Конструктор для Dependency Injection (Впровадження залежностей).
     * Spring бачить, що контролеру для роботи потрібен OrderService,
     * знаходить відповідний bean (екземпляр) у своєму контексті та передає його сюди.
     * Це найкраща практика для зв'язування компонентів у Spring.
     * @param orderService екземпляр сервісу, наданий Spring.
     */
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Метод, що обробляє HTTP GET запити на кореневий URL ("/").
     * Анотація @GetMapping("/") зв'язує URL та метод.
     * @return список замовлень, який буде перетворено на JSON.
     */
    @GetMapping("/")
    public List<Order> getOrders() {
        // Контролер не виконує логіку сам, а делегує виклик сервісному шару.
        return orderService.findAll();
    }
}
