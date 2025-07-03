package com.project.marketplace.resources;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.project.marketplace.entities.Address;
import com.project.marketplace.services.AddressService;
import com.project.marketplace.view.View;

@RestController
@RequestMapping(value = "/addresses")
public class AddressResource {

	@Autowired
	private AddressService service;

	@GetMapping
	@JsonView({ View.Address.class })
	@PreAuthorize("hasAuthority('Admin')")
	public ResponseEntity<List<Address>> findAll() {
		List<Address> list = service.findAll();
		return ResponseEntity.ok().body(list);
	}

	@GetMapping(value = "/{id}")
	@JsonView({ View.AddressById.class })
	@PreAuthorize("hasAuthority('Admin') || @addressRepository.findAddressByIdOrThrow(#id).getCustomer().getKeycloakId() == authentication.token.claims['sub']")
	public ResponseEntity<Address> findById(@PathVariable Long id) {
		Address obj = service.findById(id);
		return ResponseEntity.ok().body(obj);
	}

	@PostMapping()
	@JsonView({ View.AddressById.class })
	@PreAuthorize("hasAuthority('Admin') || @customerRepository.findCustomerByIdOrThrow(#customerId).getKeycloakId() == authentication.token.claims['sub']")
	public Address insert(@RequestBody Address address, @RequestParam Long customerId) {
		return service.insert(customerId, address);
	}

	@DeleteMapping(value = "/{id}")
	@JsonView({ View.AddressById.class })
	@PreAuthorize("hasAuthority('Admin') || @addressRepository.findAddressByIdOrThrow(#id).getCustomer().getKeycloakId() == authentication.token.claims['sub']")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}

	@PatchMapping(value = "/{id}")
	@JsonView({ View.AddressById.class })
	@PreAuthorize("hasAuthority('Admin') || @customerRepository.findCustomerByIdOrThrow(#customerId).getKeycloakId() == authentication.token.claims['sub']")
	public ResponseEntity<Address> update(@RequestParam Long customerId, @PathVariable Long id,
			@RequestBody Address obj) {
		obj = service.update(customerId, id, obj);
		return ResponseEntity.ok().body(obj);
	}
}
