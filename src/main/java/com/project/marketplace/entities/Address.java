package com.project.marketplace.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonView;
import com.project.marketplace.entities.enums.AddressType;
import com.project.marketplace.view.View;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_address")
public class Address implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonView({ View.CustomersById.class, View.OrdersById.class })
	private Long id;

	@JsonView({ View.CustomersById.class, View.OrdersById.class })
	private String street;

	@JsonView({ View.CustomersById.class, View.OrdersById.class })
	private int houseNumber;

	@JsonView({ View.CustomersById.class, View.OrdersById.class })
	private String neighborhood;

	@JsonView({ View.CustomersById.class, View.OrdersById.class })
	private int zipCode;

	@JsonView({ View.CustomersById.class, View.OrdersById.class })
	private String country;

	@JsonView({ View.CustomersById.class, View.OrdersById.class })
	private AddressType addressType;

	@ManyToOne
	@JoinColumn(name = "customer_id")
	private Customer customer;

	@OneToMany(mappedBy = "deliveryAddress")
	private List<Order> order = new ArrayList<>();

	public Address() {
	}

	public Address(Long id, String street, int houseNumber, String neighborhood, int zipCode, String country,
			AddressType addressType, Customer client) {
		super();
		this.id = id;
		this.street = street;
		this.houseNumber = houseNumber;
		this.neighborhood = neighborhood;
		this.zipCode = zipCode;
		this.country = country;
		setAddressType(addressType);
		this.customer = client;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public int getHouseNumber() {
		return houseNumber;
	}

	public void setHouseNumber(int houseNumber) {
		this.houseNumber = houseNumber;
	}

	public String getNeighborhood() {
		return neighborhood;
	}

	public void setNeighborhood(String neighborhood) {
		this.neighborhood = neighborhood;
	}

	public int getZipCode() {
		return zipCode;
	}

	public void setZipCode(int zipCode) {
		this.zipCode = zipCode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public AddressType getAddressType() {
		return addressType;
	}

	public void setAddressType(AddressType addressType) {
		this.addressType = addressType;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Address other = (Address) obj;
		return Objects.equals(id, other.id);
	}

}
