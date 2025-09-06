package com.restaurant.orderservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Сутність, що представляє позицію в меню.
 *
 * @Entity - вказує, що цей клас є сутністю JPA.
 * @Table(name = "menu_items") - вказує, що сутність буде збережена в таблиці з назвою "menu_items".
 * @Data - анотація Lombok для генерації гетерів, сетерів і т.д.
 */
@Entity
@Table(name = "menu_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuItem {

    public MenuItem(String name, String description, BigDecimal price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String description;

    /**
     * Ціна позиції. Використовуємо BigDecimal для точних фінансових розрахунків,
     * щоб уникнути проблем з округленням, які виникають при використанні float або double.
     */
    private BigDecimal price;
}
