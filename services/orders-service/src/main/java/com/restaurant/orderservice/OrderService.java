package com.restaurant.orderservice;

import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервісний шар (Service Layer).
 * Цей клас інкапсулює бізнес-логіку, пов'язану з операціями над замовленнями.
 * Він виступає як посередник між контролером (API) та джерелом даних (наприклад, базою даних).
 *
 * Анотація @Service повідомляє Spring, що цей клас є компонентом-сервісом,
 * і Spring має створити його екземпляр (bean) та керувати його життєвим циклом.
 */
@Service
public class OrderService {

    /**
     * Метод для отримання всіх замовлень.
     * На даному етапі він повертає статичний список-заглушку.
     * У реальному додатку тут був би код для звернення до бази даних.
     * @return список замовлень.
     */
    public List<Order> findAll() {
        return List.of(
            new Order(1, "Піца Маргарита"),
            new Order(2, "Цезар з куркою")
        );
    }

    /**
     * Метод для пошуку страви за її унікальним ідентифікатором (ID).
     * @param id ідентифікатор страви для пошуку.
     * @return Optional, що містить знайдене замовлення, або порожній Optional.
     */
    public java.util.Optional<Order> findDishById(Integer id) {
        return findAll().stream()
                .filter(order -> order.id().equals(id))
                .findFirst();
    }
}
