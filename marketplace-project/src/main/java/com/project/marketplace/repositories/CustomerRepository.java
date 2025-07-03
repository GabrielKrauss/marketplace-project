package com.project.marketplace.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.project.marketplace.entities.Customer;
import com.project.marketplace.services.exceptions.ResourceNotFoundException;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
	boolean existsByDocumentNumber(String documentNumber);
	Customer findByDocumentNumber(String documentNumber);
	Customer findByKeycloakId(String keycloakId);
	
	@Query("SELECT c FROM Customer c WHERE c.isDeleted IS FALSE")
	List<Customer> findAllActiveCustomers();

	@Query("SELECT c FROM Customer c WHERE c.id = ?1")
    default Customer findCustomerByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));
    }
	
}
