package com.project.marketplace.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.marketplace.entities.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
	List<Product> findByName(String name);
}
