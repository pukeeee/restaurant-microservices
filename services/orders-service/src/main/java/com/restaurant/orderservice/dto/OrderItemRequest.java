package com.restaurant.orderservice.dto;

import lombok.Data;

/**
 * Data Transfer Object (DTO) для однієї позиції в запиті на створення замовлення.
 * Цей клас представляє, як клієнт (наприклад, frontend-додаток)
 * повинен надіслати інформацію про одну замовлену страву.
 */
@Data
public class OrderItemRequest {

    /**
     * Унікальний ідентифікатор позиції меню (наприклад, страви), яку замовляють.
     */
    private Integer menuItemId;

    /**
     * Кількість замовлених одиниць цієї позиції.
     */
    private int quantity;
}
