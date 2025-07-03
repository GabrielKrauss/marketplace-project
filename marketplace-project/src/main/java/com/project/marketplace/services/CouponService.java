package com.project.marketplace.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.project.marketplace.entities.Coupon;
import com.project.marketplace.entities.Customer;
import com.project.marketplace.repositories.CouponRepository;
import com.project.marketplace.services.exceptions.DatabaseException;
import com.project.marketplace.services.exceptions.InvalidDataException;
import com.project.marketplace.services.exceptions.ObjectAlreadyExistsException;
import com.project.marketplace.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CouponService {

	@Autowired
	private CouponRepository repository;

	public List<Coupon> findAll() {
		return repository.findAll();
	}
	
	public List<Coupon> findAllActive() {
		return repository.findAllActiveCoupons();
	}

	public Coupon findById(Long id) {
		Coupon obj = repository.findById(id)
	            .orElseThrow(() -> new ResourceNotFoundException("Customer Doesn't Exist. Id: " + id));
		return obj;
	}
	
	public Coupon findByCode(String code) {
		Coupon obj = repository.findByCode(code)
	            .orElseThrow(() -> new ResourceNotFoundException("Customer Doesn't Exist. Id: " + code));
		return obj;
	}	

	public Coupon insert(Coupon obj) {
		if (repository.existsByCode(obj.getCode())) {
			throw new ObjectAlreadyExistsException(obj.getCode());
		}
		if(obj.getDiscountPercentage() != 0.0 && obj.getDiscountValue() != 0.0) {
			throw new InvalidDataException("Coupon shouldn't have both value and percentage discount.");
		}
		return repository.save(obj);
	}

	public void delete(Long id) {
		try {
			Coupon coupon = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Coupon Doesn't Exist. Id: " + id));
			coupon.setIsActive(false);
			repository.save(coupon);
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException(e.getMessage());
		}
	}

	public Coupon update(Long id, Coupon obj) {
		try {
			Coupon entity = repository.getReferenceById(id);
			if (repository.existsByCode(obj.getCode())) {
				if(repository.findByCode(obj.getCode()).get().getId() != id) {
					throw new ObjectAlreadyExistsException(obj.getCode());
				}
			}
			if(obj.getDiscountPercentage() != 0.0 && obj.getDiscountValue() != 0.0) {
				throw new InvalidDataException("Coupon shouldn't have both value and percentage discount.");
			}
			updateData(entity, obj);
			return repository.save(entity);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException(id);
		}
	}

	protected void updateData(Coupon entity, Coupon obj) {
		entity.setCode((obj.getCode() != null) ? obj.getCode() : entity.getCode());
		entity.setDiscountPercentage((obj.getDiscountPercentage() != 0.0) ? obj.getDiscountPercentage() : entity.getDiscountPercentage());
		entity.setDiscountValue((obj.getDiscountValue() != 0.0) ? obj.getDiscountValue() : entity.getDiscountValue());
		entity.setIsActive((obj.getIsActive() != null) ? obj.getIsActive() : entity.getIsActive());
	}

}
