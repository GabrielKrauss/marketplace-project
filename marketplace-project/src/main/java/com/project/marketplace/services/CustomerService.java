package com.project.marketplace.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.project.marketplace.entities.Customer;
import com.project.marketplace.repositories.CustomerRepository;
import com.project.marketplace.services.exceptions.DatabaseException;
import com.project.marketplace.services.exceptions.ObjectAlreadyExistsException;
import com.project.marketplace.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CustomerService {

	@Autowired
	private CustomerRepository repository;

	public List<Customer> findAllActive() {
		return repository.findAllActiveCustomers();
	}

	public List<Customer> findAll() {
		return repository.findAll();
	}

	public Customer findById(Long id) {
		Customer obj = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Customer Doesn't Exist. Id: " + id));
		return obj;
	}

	public Customer findByKeycloakId(String keycloakId) {
		return repository.findByKeycloakId(keycloakId);
	}

	public Customer insert(Customer obj) {
	    obj.setIsDeleted(false);

	    if (repository.existsByDocumentNumber(obj.getDocumentNumber())) {
	        Customer existingCustomer = repository.findByDocumentNumber(obj.getDocumentNumber());
	        if (existingCustomer.getIsDeleted()) {
	            existingCustomer.setIsDeleted(false);
				existingCustomer.setKeycloakId(obj.getKeycloakId());
	            return repository.save(existingCustomer);
	        } else {
	            throw new ObjectAlreadyExistsException("Customer with document number " + obj.getDocumentNumber());
	        }
	    }

	    return repository.save(obj);
	}

	public void delete(Long id) {
		try {
			Customer obj = repository.findById(id)
					.orElseThrow(() -> new ResourceNotFoundException("Customer Doesn't Exist. Id: " + id));
			obj.setIsDeleted(true);
			repository.save(obj);
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException(e.getMessage());
		}
	}

	public Customer update(Long id, Customer obj) {
		try {
			Customer entity = repository.getReferenceById(id);
			Customer existingCustomer = repository.findByDocumentNumber(obj.getDocumentNumber());
			if (existingCustomer != null && !existingCustomer.getId().equals(id)) {
				throw new ObjectAlreadyExistsException(
						"Customer with document number " + obj.getDocumentNumber() + " already exists");
			}
			updateData(entity, obj);
			return repository.save(entity);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException(id);
		}
	}

	protected void updateData(Customer entity, Customer obj) {
		entity.setName((obj.getName() != null) ? obj.getName() : entity.getName());
		entity.setPhone((obj.getPhone() != null) ? obj.getPhone() : entity.getPhone());
		entity.setDocumentNumber(
				(obj.getDocumentNumber() != null) ? obj.getDocumentNumber() : entity.getDocumentNumber());
		entity.setCustomerType((obj.getCustomerType() != null) ? obj.getCustomerType() : entity.getCustomerType());
		entity.setCreditScore((obj.getCreditScore() != null) ? obj.getCreditScore() : entity.getCreditScore());
		entity.setLibrary((obj.getLibrary() != null) ? obj.getLibrary() : entity.getLibrary());
		entity.setIsDeleted((obj.getIsDeleted() != null) ? obj.getIsDeleted() : entity.getIsDeleted());
	}

}
