package com.restaurant.orderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}

@RestController
class OrderController {

    @GetMapping("/")
    public String getOrders() {
        // Повертаємо заглушку. Пізніше тут буде логіка отримання замовлень.
        return "Привіт від сервісу замовлень!";
    }
}
