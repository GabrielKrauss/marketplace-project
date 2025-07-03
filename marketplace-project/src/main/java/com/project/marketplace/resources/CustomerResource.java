package com.project.marketplace.resources;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
	@JsonView({ View.Customers.class })
	@PreAuthorize("hasAuthority('Admin')")
	public ResponseEntity<List<Customer>> findAll() {
		List<Customer> list = service.findAll();
		return ResponseEntity.ok().body(list);
	}
	
	@GetMapping(value = "/activeCustomers")
	@JsonView({ View.Customers.class })
	@PreAuthorize("hasAuthority('Admin')")
	public ResponseEntity<List<Customer>> findAllActive() {
		List<Customer> list = service.findAllActive();
		return ResponseEntity.ok().body(list);
	}

	@GetMapping(value = "/{id}")
	@JsonView({ View.CustomersById.class })
	@PreAuthorize("hasAuthority('Admin') || @customerRepository.findCustomerByIdOrThrow(#id).getKeycloakId() == authentication.token.claims['sub']")
	public ResponseEntity<Customer> findById(@PathVariable Long id) {
		Customer obj = service.findById(id);
		return ResponseEntity.ok().body(obj);
	}
	
	@GetMapping("/keycloak/{keycloakId}")
	@JsonView({ View.CustomersById.class })
	@PreAuthorize("hasAuthority('Admin') || #keycloakId == authentication.token.claims['sub']")
	public ResponseEntity<Customer> findByKeycloakId(@PathVariable String keycloakId) {
	    Customer customer = service.findByKeycloakId(keycloakId);
	    if (customer != null) {
	        return ResponseEntity.ok(customer);
	    } else {
	        return ResponseEntity.notFound().build();
	    }
	}

	@PostMapping
	@JsonView({ View.CustomersById.class })
	@PreAuthorize("hasAuthority('Operator') || @customerRepository.findByKeycloakId(#keycloakId).getKeycloakId() == authentication.token.claims['sub']")
	public ResponseEntity<Customer> insert(@RequestBody Customer obj) {
		obj = service.insert(obj);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getId()).toUri();
		return ResponseEntity.created(uri).body(obj);
	}

	@DeleteMapping(value = "/{id}")
	@JsonView({ View.CustomersById.class })
	@PreAuthorize("hasAuthority('Admin') || @customerRepository.findCustomerByIdOrThrow(#id).getKeycloakId() == authentication.token.claims['sub']")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}

	@PatchMapping(value = "/{id}")
	@JsonView({ View.CustomersById.class })
	@PreAuthorize("hasAuthority('Admin') || @customerRepository.findCustomerByIdOrThrow(#id).getKeycloakId() == authentication.token.claims['sub']")
	public ResponseEntity<Customer> update(@PathVariable Long id, @RequestBody Customer obj) {
		obj = service.update(id, obj);
		return ResponseEntity.ok().body(obj);
	}
}
