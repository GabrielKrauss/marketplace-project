package com.project.marketplace.services;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.marketplace.entities.Category;
import com.project.marketplace.entities.Product;
import com.project.marketplace.repositories.CategoryRepository;
import com.project.marketplace.repositories.ProductRepository;

@Service
public class ProductService {

	@Autowired
	private ProductRepository repository;
	
	@Autowired
	private CategoryRepository categoryRepository;

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
			obj.setCategories(categories);
		}
		return repository.save(obj);
	}
}
