package com.project.marketplace.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.marketplace.entities.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
	List<Product> findByName(String name);

	@Query("SELECT p FROM Product p WHERE p.isDeleted = FALSE")
	Page<Product> findAllActiveProducts(Pageable pageable);

	@Query("SELECT p FROM Product p WHERE "
			+ "(:searchTerm IS NULL OR :searchTerm = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) "
			+ "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) "
			+ "AND (:categoryIds IS NULL OR EXISTS (SELECT 1 FROM p.categories c WHERE c.id IN :categoryIds)) "
			+ "AND p.isDeleted = FALSE")
	Page<Product> findFilteredActiveProducts(@Param("searchTerm") String searchTerm,
			@Param("categoryIds") List<Long> categoryIds, Pageable pageable);

	@Query("SELECT p FROM Product p WHERE "
			+ "(:searchTerm IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) "
			+ " OR LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) "
			+ "AND (:categoryIds IS NULL OR EXISTS (SELECT 1 FROM p.categories c WHERE c.id IN :categoryIds))")
	Page<Product> findFilteredProducts(@Param("searchTerm") String searchTerm,
			@Param("categoryIds") List<Long> categoryIds, Pageable pageable);
}
