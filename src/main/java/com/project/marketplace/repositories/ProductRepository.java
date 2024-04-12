package com.project.marketplace.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.marketplace.entities.Product;


public interface ProductRepository extends JpaRepository<Product, Long> {

}
