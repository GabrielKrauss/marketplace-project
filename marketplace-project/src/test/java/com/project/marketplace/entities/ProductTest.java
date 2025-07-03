package com.project.marketplace.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import org.junit.jupiter.api.Test;

class ProductTest {

    @Test
    void getOrders_shouldReturnOrdersForProduct() {
        Product product = new Product(1L, "Product", "Description", 100.0, 10, true, true, null, null);

        Order order = new Order(); 
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        product.getItems().add(orderItem);
        Set<Order> orders = product.getOrders();
        
        assertEquals(1, orders.size());
    }
}
