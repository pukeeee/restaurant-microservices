package com.restaurant.orderservice.service;

import com.restaurant.orderservice.dto.CreateOrderRequest;
import com.restaurant.orderservice.dto.OrderItemRequest;
import com.restaurant.orderservice.model.MenuItem;
import com.restaurant.orderservice.model.Order;
import com.restaurant.orderservice.model.OrderItem;
import com.restaurant.orderservice.repository.MenuItemRepository;
import com.restaurant.orderservice.repository.OrderRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;

    @Autowired
    public OrderService(
        OrderRepository orderRepository,
        MenuItemRepository menuItemRepository
    ) {
        this.orderRepository = orderRepository;
        this.menuItemRepository = menuItemRepository;
    }

    public Optional<Order> findOrderById(Integer id) {
        return orderRepository.findById(id);
    }

    public List<MenuItem> findAllMenuItems() {
        return menuItemRepository.findAll();
    }

    public List<Order> findOrdersByUserId(Integer userId) {
        return orderRepository.findByUserId(userId);
    }

    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        // 1. Дістаємо ID всіх замовлених страв
        List<Integer> menuItemIds = request
            .getItems()
            .stream()
            .map(OrderItemRequest::getMenuItemId)
            .toList();

        // 2. Дістаємо з БД всі MenuItem за їх ID, щоб перевірити наявність та отримати актуальні ціни
        List<MenuItem> menuItems = menuItemRepository.findAllById(menuItemIds);
        if (menuItemIds.size() != menuItems.size()) {
            throw new IllegalArgumentException(
                "Одна або декілька позицій меню не знайдені в базі даних"
            );
        }

        // 3. Створюємо мапу для швидкого доступу до MenuItem за ID
        Map<Integer, MenuItem> menuItemMap = menuItems
            .stream()
            .collect(Collectors.toMap(MenuItem::getId, menuItem -> menuItem));

        // 4. Створюємо новий об'єкт замовлення
        Order newOrder = new Order();
        newOrder.setUserId(request.getUserId());
        newOrder.setStatus("PLACED"); // Початковий статус

        // 5. Створюємо список позицій замовлення (OrderItem)
        List<OrderItem> orderItems = request
            .getItems()
            .stream()
            .map(itemRequest ->
                mapToOrderItem(itemRequest, menuItemMap, newOrder)
            )
            .toList();

        // 6. Розраховуємо загальну вартість
        BigDecimal totalPrice = orderItems
            .stream()
            .map(item ->
                item
                    .getPricePerItem()
                    .multiply(BigDecimal.valueOf(item.getQuantity()))
            )
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 7. Встановлюємо фінальні дані для замовлення
        newOrder.setTotalPrice(totalPrice);
        newOrder.setOrderItems(orderItems);

        // 8. Зберігаємо замовлення в БД. Завдяки CascadeType.ALL, пов'язані OrderItem збережуться автоматично.
        return orderRepository.save(newOrder);
    }

    private OrderItem mapToOrderItem(
        OrderItemRequest itemRequest,
        Map<Integer, MenuItem> menuItemMap,
        Order newOrder
    ) {
        MenuItem menuItem = menuItemMap.get(itemRequest.getMenuItemId());
        OrderItem orderItem = new OrderItem();
        orderItem.setMenuItem(menuItem);
        orderItem.setQuantity(itemRequest.getQuantity());
        orderItem.setPricePerItem(menuItem.getPrice()); // Фіксуємо ціну на момент замовлення
        orderItem.setOrder(newOrder); // Встановлюємо зворотній зв'язок
        return orderItem;
    }
}
