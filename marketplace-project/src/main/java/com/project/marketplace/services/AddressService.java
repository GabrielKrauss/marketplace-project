package com.project.marketplace.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.project.marketplace.entities.Address;
import com.project.marketplace.entities.Coupon;
import com.project.marketplace.entities.Customer;
import com.project.marketplace.repositories.AddressRepository;
import com.project.marketplace.repositories.CustomerRepository;
import com.project.marketplace.services.exceptions.DatabaseException;
import com.project.marketplace.services.exceptions.ObjectAlreadyExistsException;
import com.project.marketplace.services.exceptions.ResourceNotAvailableException;
import com.project.marketplace.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class AddressService {

	@Autowired
	private AddressRepository repository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private CustomerService customerService;

	public List<Address> findAll() {
		return repository.findAll();
	}

	public Address findById(Long id) {
		Address obj = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Customer Doesn't Exist. Id: " + id));
		return obj;
	}

	public Address insert(Long customerId, Address obj) {
		Customer customer = customerService.findById(customerId);
		obj.setCustomer(customer);
		return repository.save(obj);
	}

	public void delete(Long id) {
		try {
			Address address = repository.findById(id)
					.orElseThrow(() -> new ResourceNotFoundException("Address Doesn't Exist. Id: " + id));
			address.setIsActive(false);
			repository.save(address);

		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException(e.getMessage());
		}
	}

	public Address update(Long customerId, Long objId, Address obj) {
		try {
			Customer customer = customerRepository.findById(customerId)
					.orElseThrow(() -> new ResourceNotFoundException("Customer Doesn't Exist. Id: " + customerId));

			Address entity = repository.getReferenceById(objId);
			updateData(entity, obj);
			return repository.save(entity);

		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException(objId);
		}
	}

	protected void updateData(Address entity, Address obj) {
		entity.setAddressType((obj.getAddressType() != null) ? obj.getAddressType() : entity.getAddressType());
		entity.setCountry((obj.getCountry() != null) ? obj.getCountry() : entity.getCountry());
		entity.setHouseNumber((obj.getHouseNumber() != 0) ? obj.getHouseNumber() : entity.getHouseNumber());
		entity.setNeighborhood((obj.getNeighborhood() != null) ? obj.getNeighborhood() : entity.getNeighborhood());
		entity.setStreet((obj.getStreet() != null) ? obj.getStreet() : entity.getStreet());
		entity.setZipCode((obj.getZipCode() != 0) ? obj.getZipCode() : entity.getZipCode());
	}

}
