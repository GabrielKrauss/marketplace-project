package com.project.marketplace.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.marketplace.entities.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

}
