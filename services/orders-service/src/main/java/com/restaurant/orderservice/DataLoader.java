package com.restaurant.orderservice;

import com.restaurant.orderservice.model.MenuItem;
import com.restaurant.orderservice.repository.MenuItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Компонент для завантаження початкових даних у базу даних при старті додатку.
 * Це зручний спосіб для розробки та тестування, щоб БД не була порожньою.
 *
 * @Component - позначає цей клас як Spring-компонент, щоб він був автоматично знайдений та зареєстрований.
 * CommandLineRunner - це інтерфейс, який вимагає реалізації методу run().
 *                   Spring Boot автоматично виконає цей метод після запуску додатку.
 */
@Component
public class DataLoader implements CommandLineRunner {

    private final MenuItemRepository menuItemRepository;

    public DataLoader(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Перевіряємо, чи є вже дані в таблиці меню, щоб не дублювати їх при кожному перезапуску
        if (menuItemRepository.count() == 0) {
            System.out.println("База даних меню порожня. Завантажую початкові дані...");

            MenuItem pizza = new MenuItem(null, "Піца Маргарита", "Класична піца з томатним соусом та моцарелою", new BigDecimal("150.00"));
            MenuItem caesar = new MenuItem(null, "Салат Цезар", "Салат з куркою, грінками та соусом Цезар", new BigDecimal("120.50"));
            MenuItem steak = new MenuItem(null, "Стейк Рібай", "Соковитий стейк з яловичини середнього прожарювання", new BigDecimal("350.75"));

            menuItemRepository.saveAll(List.of(pizza, caesar, steak));

            System.out.println("Початкові дані для меню успішно завантажені.");
        } else {
            System.out.println("База даних меню вже містить дані. Завантаження не потрібне.");
        }
    }
}
