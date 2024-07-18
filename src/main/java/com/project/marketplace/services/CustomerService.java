package com.project.marketplace.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.marketplace.entities.Address;
import com.project.marketplace.entities.Customer;
import com.project.marketplace.entities.User;
import com.project.marketplace.repositories.AddressRepository;
import com.project.marketplace.repositories.CustomerRepository;
import com.project.marketplace.repositories.UserRepository;

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
}
