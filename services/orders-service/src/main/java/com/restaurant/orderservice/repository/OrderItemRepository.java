package com.restaurant.orderservice.repository;

import com.restaurant.orderservice.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторій для роботи з сутностями OrderItem.
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
}
