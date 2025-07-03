package com.project.marketplace.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.project.marketplace.entities.Address;
import com.project.marketplace.entities.Customer;
import com.project.marketplace.entities.Product;
import com.project.marketplace.services.exceptions.ResourceNotFoundException;

public interface AddressRepository extends JpaRepository<Address, Long> {
	boolean existsByStreet(String street);
	List<Address> findByCustomer(Customer customer);
	
	@Query("SELECT a FROM Address a WHERE a.id = ?1")
    default Address findAddressByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new ResourceNotFoundException("Address not found with ID: " + id));
    }
}
