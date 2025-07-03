package com.project.marketplace.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.project.marketplace.entities.Coupon;
import com.project.marketplace.entities.Customer;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
	boolean existsByCode(String code);
	Optional<Coupon> findByCode(String code);
	
	@Query("SELECT c FROM Coupon c WHERE c.isActive IS TRUE")
	List<Coupon> findAllActiveCoupons();
}
