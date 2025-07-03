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
import com.project.marketplace.entities.Category;
import com.project.marketplace.entities.Coupon;
import com.project.marketplace.entities.Customer;
import com.project.marketplace.services.CouponService;
import com.project.marketplace.view.View;

@RestController
@RequestMapping(value = "/coupons")
public class CouponResource {

	@Autowired
	private CouponService service;

	@GetMapping
	@JsonView({ View.Coupons.class })
	@PreAuthorize("hasAuthority('Admin')")
	public ResponseEntity<List<Coupon>> findAll() {
		List<Coupon> list = service.findAll();
		return ResponseEntity.ok().body(list);
	}

	@GetMapping(value = "/id/{id}")
	@JsonView({ View.CouponsById.class })
	@PreAuthorize("hasAuthority('Admin')")
	public ResponseEntity<Coupon> findById(@PathVariable Long id) {
	    Coupon obj = service.findById(id);
	    return ResponseEntity.ok().body(obj);
	}

	@GetMapping(value = "/code/{code}")
	@JsonView({ View.CouponsById.class })
	public ResponseEntity<Coupon> findByCode(@PathVariable String code) {
	    Coupon obj = service.findByCode(code);
	    return ResponseEntity.ok().body(obj);
	}

	@GetMapping(value = "/activeCoupons")
	@JsonView({ View.Coupons.class })
	@PreAuthorize("hasAuthority('Admin')")
	public ResponseEntity<List<Coupon>> findAllActive() {
		List<Coupon> list = service.findAllActive();
		return ResponseEntity.ok().body(list);
	}
	
	@PostMapping
	@JsonView({ View.Coupons.class })
	@PreAuthorize("hasAuthority('Admin')")
	public ResponseEntity<Coupon> insert(@RequestBody Coupon obj) {
		obj = service.insert(obj);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getId()).toUri();
		return ResponseEntity.created(uri).body(obj);
	}
	
	@DeleteMapping(value = "/{id}")
	@JsonView({ View.CouponsById.class })
	@PreAuthorize("hasAuthority('Admin')")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}

	@PatchMapping(value = "/{id}")
	@JsonView({ View.CouponsById.class })
	@PreAuthorize("hasAuthority('Admin')")
	public ResponseEntity<Coupon> update(@PathVariable Long id, @RequestBody Coupon obj) {
		obj = service.update(id, obj);
		return ResponseEntity.ok().body(obj);
	}
}
