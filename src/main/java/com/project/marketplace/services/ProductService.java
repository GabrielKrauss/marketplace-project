package com.project.marketplace.services;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.project.marketplace.entities.Category;
import com.project.marketplace.entities.Order;
import com.project.marketplace.entities.Product;
import com.project.marketplace.repositories.CategoryRepository;
import com.project.marketplace.repositories.OrderItemRepository;
import com.project.marketplace.repositories.OrderRepository;
import com.project.marketplace.repositories.ProductRepository;
import com.project.marketplace.services.exceptions.DatabaseException;
import com.project.marketplace.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ProductService {

	@Autowired
	private ProductRepository repository;
	
	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private OrderRepository orderRepository;
	
	public List<Product> findAll() {
		return repository.findAll();
	}

	public Product findById(Long id) {
		Optional<Product> obj = repository.findById(id);
		return obj.get();
	}
	
	public Product insert(Product obj) {
		Set<Category> categories = new HashSet<>();
		for (Long x : obj.getCategoriesId()) {
			categories.add(categoryRepository.findById(x).get());
		}
		obj.setCategories(categories);
		return repository.save(obj);
	}
	
	public void delete(Long id) {
		try {
			if (!repository.existsById(id)) {
				throw new ResourceNotFoundException(id);
			}
			for(Order order : repository.findById(id).get().getOrders()) {
				orderRepository.deleteById(order.getId());
			}
			repository.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException(e.getMessage());
		}
	}

	public Product update(Long id, Product obj) {
		try {
			Product entity = repository.getReferenceById(id);
			updateData(entity, obj);
			return repository.save(entity);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException(id);
		}

	}

	private void updateData(Product entity, Product obj) {
		entity.setName(obj.getName());
		entity.setUnitPrice(obj.getUnitPrice());
		entity.setIsPhysical(obj.getIsPhysical());
		entity.setDescription(obj.getDescription());
		entity.setSellIndicator(obj.getSellIndicator());
		entity.setCategories(obj.getCategories());
	}
}
