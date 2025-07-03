package com.project.marketplace.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;
import com.project.marketplace.entities.enums.CustomerType;
import com.project.marketplace.view.View;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tb_customer")
public class Customer implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	@JsonView({ View.Customers.class, View.Orders.class, View.Products.class })
	private Long id;

	@JsonView({ View.Customers.class, View.OrdersById.class, View.Products.class })
	private String name;
	
	@JsonView({ View.Customers.class, View.OrdersById.class, View.Products.class })
	private String keycloakId;

	@JsonView({ View.Customers.class, View.OrdersById.class, View.Products.class })
	private String phone;

	@JsonView({ View.Customers.class, View.OrdersById.class, View.Products.class })
	private String email;

	@JsonView({ View.Customers.class })
	private String documentNumber;

	@JsonView({ View.Customers.class })
	private String creditScore;

	@Column(name = "is_deleted")
	@JsonView({ View.Customers.class })
	private Boolean isDeleted;

	@JsonView({ View.Customers.class })
	private CustomerType customerType;

	@OneToMany(mappedBy = "customer")
	@JsonView({ View.CustomersById.class })
	private List<Order> orders = new ArrayList<>();

	@OneToMany(mappedBy = "customer")
	@JsonView({ View.CustomersById.class })
	private List<Address> addresses = new ArrayList<>();

	@ManyToMany
	@JsonView({ View.CustomersById.class })
	@JoinTable(name = "tb_library", joinColumns = @JoinColumn(name = "customer_id"), inverseJoinColumns = @JoinColumn(name = "product_id"))
	private List<Product> library = new ArrayList<>();

	public Customer(Long id, String name, String phone, String documentNumber, String creditScore,
			CustomerType customerType, List<Address> addresses, List<Product> library) {
		super();
		this.id = id;
		this.name = name;
		this.phone = phone;
		this.documentNumber = documentNumber;
		this.creditScore = creditScore;
		setCustomerType(customerType);
		this.addresses = addresses;
		this.library = library;
		this.isDeleted = false;
	}

	public CustomerType getCustomerType() {
		return customerType;
	}

	public void setCustomerType(CustomerType customerType) {
		this.customerType = customerType;
	}

}
