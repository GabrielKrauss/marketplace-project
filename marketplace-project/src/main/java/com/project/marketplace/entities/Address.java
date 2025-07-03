package com.project.marketplace.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "tb_address")
public class Address implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	@JsonView({ View.CustomersById.class, View.Orders.class, View.Address.class })
	private Long id;

	@JsonView({ View.CustomersById.class, View.OrdersById.class, View.Address.class })
	private String street;

	@JsonView({ View.CustomersById.class, View.OrdersById.class, View.Address.class })
	private int houseNumber;

	@JsonView({ View.CustomersById.class, View.OrdersById.class, View.Address.class })
	private String neighborhood;

	@JsonView({ View.CustomersById.class, View.OrdersById.class, View.Address.class })
	private int zipCode;

	@JsonView({ View.CustomersById.class, View.OrdersById.class, View.Address.class })
	private String country;
	
	@JsonView({ View.CustomersById.class, View.OrdersById.class, View.Address.class })
	private String city;
	
	@JsonView({ View.CustomersById.class, View.OrdersById.class, View.Address.class })
	private Boolean isActive;

	@JsonView({ View.CustomersById.class, View.OrdersById.class, View.Address.class })
	private AddressType addressType;

	@ManyToOne
	@JoinColumn(name = "customer_id")
	private Customer customer;

	@OneToMany(mappedBy = "deliveryAddress")
	private List<Order> order = new ArrayList<>();


	public Address(Long id, String street, int houseNumber, String neighborhood, int zipCode, String country, String city,
			AddressType addressType, Customer client) {
		super();
		this.id = id;
		this.street = street;
		this.houseNumber = houseNumber;
		this.neighborhood = neighborhood;
		this.zipCode = zipCode;
		this.country = country;
		this.city = city;
		setAddressType(addressType);
		this.customer = client;
	}

}
