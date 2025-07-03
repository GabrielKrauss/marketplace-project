package com.project.marketplace.resources;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.annotation.JsonView;
import com.project.marketplace.entities.Product;
import com.project.marketplace.services.ProductService;
import com.project.marketplace.view.View;

@RestController
@RequestMapping(value = "/products")
public class ProductResource {

	@Autowired
	private ProductService service;

	@GetMapping
	@JsonView({ View.Products.class })
	@PreAuthorize("hasAuthority('Admin')")
	public ResponseEntity<Map<String, Object>> findAll(@RequestParam(required = false) String searchTerm,
			@RequestParam(required = false) List<Long> categoryIds, Pageable pageable) {

		Page<Product> page;

		if ((searchTerm == null || searchTerm.isBlank()) && (categoryIds == null || categoryIds.isEmpty())) {
			page = service.findAll(pageable);
		} else {
			page = service.findFilteredProducts(searchTerm, categoryIds, pageable);
		}

		System.out.println("Total de produtos retornados: " + page.getTotalElements());

		Map<String, Object> response = new HashMap<>();
		response.put("content", page.getContent());
		response.put("totalPages", page.getTotalPages());
		response.put("totalElements", page.getTotalElements());
		response.put("size", page.getSize());
		response.put("number", page.getNumber());

		return ResponseEntity.ok(response);
	}

	@GetMapping(value = "/activeProducts")
	@JsonView({ View.Products.class })
	public ResponseEntity<Map<String, Object>> findAllActives(@RequestParam(required = false) String searchTerm,
			@RequestParam(required = false) List<Long> categoryIds, Pageable pageable) {

		Page<Product> page;

		if ((searchTerm == null || searchTerm.isBlank()) && (categoryIds == null || categoryIds.isEmpty())) {
			page = service.findAllActive(pageable);
		} else {
			page = service.findFilteredActiveProducts(searchTerm, categoryIds, pageable);
		}

		System.out.println("Total de produtos retornados: " + page.getTotalElements());

		Map<String, Object> response = new HashMap<>();
		response.put("content", page.getContent());
		response.put("totalPages", page.getTotalPages());
		response.put("totalElements", page.getTotalElements());
		response.put("size", page.getSize());
		response.put("number", page.getNumber());

		return ResponseEntity.ok(response);
	}

	@GetMapping(value = "/{id}")
	@JsonView({ View.ProductsById.class })
	public ResponseEntity<Product> findById(@PathVariable Long id) {
		Product obj = service.findById(id);
		return ResponseEntity.ok().body(obj);
	}

	@PostMapping
	@JsonView({ View.ProductsById.class })
	@PreAuthorize("hasAuthority('Admin')")
	public ResponseEntity<Product> insert(@RequestBody Product obj) {
		obj = service.insert(obj);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getId()).toUri();
		return ResponseEntity.created(uri).body(obj);
	}

	@DeleteMapping(value = "/{id}")
	@JsonView({ View.ProductsById.class })
	@PreAuthorize("hasAuthority('Admin')")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}

	@PatchMapping(value = "/{id}")
	@JsonView({ View.ProductsById.class })
	@PreAuthorize("hasAuthority('Admin')")
	public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody Product obj) {
		obj = service.update(id, obj);
		return ResponseEntity.ok().body(obj);
	}
}
