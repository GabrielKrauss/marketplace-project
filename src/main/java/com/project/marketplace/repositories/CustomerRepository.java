package com.project.marketplace.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.marketplace.entities.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

}
