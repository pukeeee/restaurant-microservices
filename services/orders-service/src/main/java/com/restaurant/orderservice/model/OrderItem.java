package com.restaurant.orderservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Сутність, що представляє конкретну позицію в рамках замовлення.
 */
@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Зв'язок "багато-до-одного" із замовленням.
     * @ManyToOne - вказує на тип зв'язку.
     * @JoinColumn(name = "order_id") - вказує на стовпець в таблиці "order_items",
     *                                 який є зовнішнім ключем до таблиці "orders".
     * @JsonIgnore - ця анотація потрібна, щоб уникнути нескінченної рекурсії
     *               при серіалізації в JSON (Order -> OrderItem -> Order -> ...).
     */
    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private Order order;

    /**
     * Зв'язок "багато-до-одного" з позицією меню.
     * Ми зберігаємо тут пряме посилання на MenuItem,
     * а не просто menuItemId, щоб легко отримувати повну інформацію про страву.
     */
    @ManyToOne
    @JoinColumn(name = "menu_item_id")
    private MenuItem menuItem;

    private int quantity;

    private BigDecimal pricePerItem;
}
