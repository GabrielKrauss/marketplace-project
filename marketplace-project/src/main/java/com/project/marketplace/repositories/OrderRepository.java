package com.project.marketplace.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.project.marketplace.entities.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

	@Query("SELECT o FROM Order o WHERE o.isDeleted IS FALSE")
	List<Order> findAllActiveOrders();

	@Query("SELECT o FROM Order o WHERE o.isDeleted IS FALSE AND o.customer.id = ?1")
	List<Order> findAllActivesByCustomerId(Long customerId);
}
