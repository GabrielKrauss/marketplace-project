package com.project.marketplace.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.marketplace.entities.Order;


public interface OrderRepository extends JpaRepository<Order, Long> {

}
