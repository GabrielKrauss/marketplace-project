package com.project.marketplace.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.marketplace.entities.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {

}
