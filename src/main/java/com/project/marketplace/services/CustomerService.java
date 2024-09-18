package com.project.marketplace.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.project.marketplace.entities.Address;
import com.project.marketplace.entities.Customer;
import com.project.marketplace.entities.User;
import com.project.marketplace.repositories.AddressRepository;
import com.project.marketplace.repositories.CustomerRepository;
import com.project.marketplace.repositories.UserRepository;
import com.project.marketplace.services.exceptions.DatabaseException;
import com.project.marketplace.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CustomerService {

	@Autowired
	private CustomerRepository repository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AddressRepository addressRepository;

	public List<Customer> findAll() {
		return repository.findAll();
	}

	public Customer findById(Long id) {
		Optional<Customer> obj = repository.findById(id);
		return obj.get();
	}

	public Customer insert(Customer obj) {
		User user = userRepository.findById(obj.getUserId()).get();
		obj.setUser(user);

		List<Address> addresses = new ArrayList<>();
		for (Long x : obj.getAddressesId()) {
			addresses.add(addressRepository.findById(x).get());
			obj.setAddresses(addresses);
		}

		return repository.save(obj);
	}

	public void delete(Long id) {
		try {
			if (!repository.existsById(id)) {
				throw new ResourceNotFoundException(id);
			}
			repository.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException(e.getMessage());
		}
	}

	public Customer update(Long id, Customer obj) {
		try {
			Customer entity = repository.getReferenceById(id);
			updateData(entity, obj);
			return repository.save(entity);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException(id);
		}
	}

	private void updateData(Customer entity, Customer obj) {
		entity.setName(obj.getName());
		entity.setPhone(obj.getPhone());
		entity.setCustomerType(obj.getCustomerType());
		entity.setCreditScore(obj.getCreditScore());
	}

}
