package com.restaurant.orderservice.repository;

import com.restaurant.orderservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторій для роботи з сутностями Order.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    /**
     * Знаходить всі замовлення для конкретного користувача.
     * Spring Data JPA автоматично згенерує реалізацію цього методу
     * на основі його назви (findBy + ім'я поля).
     * @param userId ID користувача.
     * @return список замовлень користувача.
     */
    List<Order> findByUserId(Integer userId);
}
