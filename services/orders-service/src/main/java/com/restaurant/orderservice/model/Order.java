package com.restaurant.orderservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Сутність, що представляє замовлення.
 */
@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer userId;

    private String status;

    private BigDecimal totalPrice;

    @CreationTimestamp
    private LocalDateTime createdAt;

    /**
     * Зв'язок "один-до-багатьох" з позиціями в замовленні.
     * @OneToMany - вказує на тип зв'язку.
     * mappedBy = "order" - говорить JPA, що поле "order" в класі OrderItem
     *                      відповідає за цей зв'язок (це поле ми створимо наступним).
     * cascade = CascadeType.ALL - означає, що всі операції (збереження, видалення)
     *                             над замовленням будуть автоматично застосовані
     *                             і до пов'язаних з ним позицій.
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;
}

