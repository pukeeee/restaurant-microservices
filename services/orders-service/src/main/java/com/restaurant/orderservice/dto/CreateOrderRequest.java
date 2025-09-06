package com.restaurant.orderservice.dto;

import java.util.List;
import lombok.Data;

/**
 * Data Transfer Object (DTO) для запиту на створення нового замовлення.
 * Цей клас агрегує всю інформацію, необхідну для створення замовлення,
 * в одному об'єкті, який буде надіслано в тілі POST-запиту.
 */
@Data
public class CreateOrderRequest {

    /**
     * Ідентифікатор користувача, який робить замовлення.
     */
    private Integer userId;

    /**
     * Список позицій, які входять до замовлення.
     */
    private List<OrderItemRequest> items;
}
