package com.restaurant.orderservice.repository;

import com.restaurant.orderservice.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторій для роботи з сутностями MenuItem.
 *
 * JpaRepository<MenuItem, Integer> надає повний набір CRUD-операцій
 * (Create, Read, Update, Delete) для сутності MenuItem,
 * де Integer - це тип первинного ключа (id).
 *
 * @Repository - позначає, що цей інтерфейс є Spring-компонентом (біном)
 *               і дозволяє Spring обробляти винятки бази даних.
 */
@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Integer> {
}
