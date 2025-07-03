package com.project.marketplace.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.project.marketplace.entities.Category;
import com.project.marketplace.repositories.CategoryRepository;
import com.project.marketplace.services.exceptions.DatabaseException;
import com.project.marketplace.services.exceptions.ObjectAlreadyExistsException;
import com.project.marketplace.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository repository;

	public List<Category> findAll() {
		return repository.findAll();
	}

	public Category findById(Long id) {
		Category obj = repository.findById(id)
		.orElseThrow(() -> new ResourceNotFoundException("Product Doesn't Exist. Id: " + id));
		return obj;
	}

	public Category insert(Category obj) {
		if (repository.existsByName(obj.getName())) {
			throw new ObjectAlreadyExistsException(obj.getName());
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

	public Category update(Long id, Category obj) {
		try {
			Category entity = repository.getReferenceById(id);
			if (repository.existsByName(obj.getName())) {
				throw new ObjectAlreadyExistsException(obj.getName());
			}
			entity.setName((obj.getName() != null) ? obj.getName() : entity.getName());
			return repository.save(entity);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException(id);
		}
	}

}
