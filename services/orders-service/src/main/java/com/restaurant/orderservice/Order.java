package com.restaurant.orderservice;

/**
 * Модель даних (Data Model), що описує сутність "Замовлення".
 * В даному контексті виступає як DTO (Data Transfer Object) - об'єкт для передачі даних.
 *
 * Використання `record` - це сучасний підхід в Java (з версії 16+),
 * що дозволяє коротко описати незмінний клас-контейнер для даних.
 * Spring Boot автоматично серіалізує об'єкти цього класу в JSON і навпаки.
 */
public record Order(Integer id, String description) {
}
