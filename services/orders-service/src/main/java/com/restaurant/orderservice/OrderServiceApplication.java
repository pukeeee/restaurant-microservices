package com.restaurant.orderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Головний клас, що є точкою входу для запуску Spring Boot додатку.
 *
 * Анотація @SpringBootApplication - це потужна "три-в-одному" анотація, яка включає:
 * 1. @Configuration: Позначає клас як джерело конфігурацій для Spring.
 * 2. @EnableAutoConfiguration: Дозволяє Spring Boot автоматично налаштовувати додаток
 *    на основі залежностей у pom.xml (наприклад, автоматично налаштовує веб-сервер Tomcat).
 * 3. @ComponentScan: Вказує Spring сканувати поточний пакет (та вкладені)
 *    на наявність інших компонентів (наприклад, @RestController, @Service).
 */
@SpringBootApplication
public class OrderServiceApplication {

    /**
     * Стандартний main-метод, з якого починається виконання будь-якої Java-програми.
     * Рядок SpringApplication.run(...) фактично створює та запускає веб-сервер
     * і весь фреймворк Spring.
     * @param args аргументи командного рядка (не використовуються в даному випадку).
     */
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}