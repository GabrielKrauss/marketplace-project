package com.project.marketplace.resources;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.project.marketplace.entities.Customer;
import com.project.marketplace.services.CustomerService;
import com.project.marketplace.view.View;

@RestController
@RequestMapping(value = "/customers")
public class CustomerResource {

	@Autowired
	private CustomerService service;
	@GetMapping
	@JsonView({View.Customers.class})
	public ResponseEntity<List<Customer>> findAll(){
		List<Customer> list = service.findAll();
		return ResponseEntity.ok().body(list);
	}
	
	@GetMapping(value = "/{id}")
	@JsonView({View.CustomersById.class})
	public ResponseEntity<Customer> findById(@PathVariable Long id){
		Customer obj = service.findById(id);
		return ResponseEntity.ok().body(obj);
	}
	
}
