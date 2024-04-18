package com.project.marketplace.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.marketplace.entities.Category;


public interface CategoryRepository extends JpaRepository<Category, Long> {

}
