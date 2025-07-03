package com.project.marketplace.services;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.marketplace.entities.Category;
import com.project.marketplace.entities.Product;
import com.project.marketplace.repositories.CategoryRepository;
import com.project.marketplace.repositories.ProductRepository;
import com.project.marketplace.services.exceptions.DatabaseException;
import com.project.marketplace.services.exceptions.ObjectAlreadyExistsException;
import com.project.marketplace.services.exceptions.ResourceNotAvailableException;
import com.project.marketplace.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ProductService {

	@Autowired
	private ProductRepository repository;

	@Autowired
	private CategoryRepository categoryRepository;

	public Page<Product> findAll(Pageable pageable) {
		return repository.findAll(pageable);
	}

	public Page<Product> findAllActive(Pageable pageable) {
		return repository.findAllActiveProducts(pageable);
	}

	public Page<Product> findFilteredActiveProducts(String searchTerm, List<Long> categoryIds, Pageable pageable) {
		return repository.findFilteredActiveProducts((searchTerm == null || searchTerm.isBlank()) ? null : searchTerm,
				(categoryIds == null || categoryIds.isEmpty()) ? null : categoryIds, pageable);
	}

	public Page<Product> findFilteredProducts(String searchTerm, List<Long> categoryIds, Pageable pageable) {
		return repository.findFilteredProducts((searchTerm == null || searchTerm.isBlank()) ? null : searchTerm,
				(categoryIds == null || categoryIds.isEmpty()) ? null : categoryIds, pageable);
	}

	public Product findById(Long id) {
		Product obj = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Product Doesn't Exist. Id: " + id));

		return obj;
	}

	public Product insert(Product obj) {
		List<Product> productsInDatabase = repository.findByName(obj.getName());
		obj.setIsDeleted(compareProducts(productsInDatabase, obj));
		Set<Category> categories = new HashSet<>();
		for (Long category : obj.getCategoriesId()) {
			categories.add(categoryRepository.findById(category).get());
		}
		for (Category category : obj.getCategories()) {
			categories.add(categoryRepository.findById(category.getId()).get());
		}
		obj.setCategories(categories);
		return repository.save(obj);
	}

	public void delete(Long id) {
		try {
			if (!repository.existsById(id)) {
				throw new ResourceNotFoundException(id);
			}
			Product obj = repository.findById(id).get();
			obj.setIsDeleted(true);
			repository.save(obj);
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException(e.getMessage());
		}
	}

	public Product update(Long id, Product obj) {
		try {
			Product entity = repository.getReferenceById(id);
			List<Product> productsInDatabase = repository.findByName(obj.getName());
			if (productsInDatabase.size() != 2) {
				entity.setIsPhysical((obj.getIsPhysical() != null) ? obj.getIsPhysical() : entity.getIsPhysical());
			}
			updateData(entity, obj);

			return repository.save(entity);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException(id);
		}

	}

	protected void updateData(Product entity, Product obj) {
		entity.setName((obj.getName() != null) ? obj.getName() : entity.getName());
		entity.setUnitPrice((obj.getUnitPrice() != null) ? obj.getUnitPrice() : entity.getUnitPrice());
		entity.setDescription((obj.getDescription() != null) ? obj.getDescription() : entity.getDescription());
		entity.setSellIndicator((obj.getSellIndicator() != null) ? obj.getSellIndicator() : entity.getSellIndicator());
		entity.setIsDeleted((obj.getIsDeleted() != null) ? obj.getIsDeleted() : entity.getIsDeleted());
		entity.setStock((obj.getStock() != null) ? obj.getStock() : entity.getStock());
		entity.setImagesUrl((obj.getImagesUrl() != null) ? obj.getImagesUrl() : entity.getImagesUrl());
		entity.setFileUrl((obj.getFileUrl() != null) ? obj.getFileUrl() : entity.getFileUrl());
	    entity.setIsPhysical((obj.getIsPhysical() != null) ? obj.getIsPhysical() : entity.getIsPhysical());

		Set<Category> categories = new HashSet<>();

		if (!obj.getCategoriesId().isEmpty() || !obj.getCategories().isEmpty()) {
			for (Long category : obj.getCategoriesId()) {
				try {
					categories.add(categoryRepository.findById(category).get());
				} catch (NoSuchElementException e) {
					throw new ResourceNotFoundException(category);
				}
			}
			for (Category category : obj.getCategories()) {
				categories.add(categoryRepository.findById(category.getId()).get());
			}
			entity.setCategories(categories);
		}
	}

	protected Boolean compareProducts(List<Product> productsInDatabase, Product product) {
		if (productsInDatabase.size() == 2) {
			if (productsInDatabase.get(0).getIsPhysical() == product.getIsPhysical()) {
				if (!productsInDatabase.get(0).getIsDeleted()) {
					throw new ObjectAlreadyExistsException(product.getName());
				}
				return false;
			} else {
				if (!productsInDatabase.get(1).getIsDeleted()) {
					throw new ObjectAlreadyExistsException(product.getName());
				}
				return false;
			}

		}
		if (productsInDatabase.size() == 1) {
			if (productsInDatabase.get(0).getIsPhysical() == product.getIsPhysical()) {
				if (!productsInDatabase.get(0).getIsDeleted()) {
					throw new ObjectAlreadyExistsException(product.getName());
				}
				return false;
			}
		}

		return false;
	}
}
