package com.project.marketplace.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonView;
import com.project.marketplace.view.View;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "tb_customer")
public class Customer implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonView({ View.Customers.class, View.Orders.class, View.Products.class })
	private Long id;

	@JsonView({ View.Customers.class, View.Orders.class, View.Products.class })
	private String name;

	@JsonView({ View.Customers.class, View.Orders.class, View.Products.class })
	private String phone;

	@JsonView({ View.Customers.class })
	private String creditScore;

	@OneToOne
	@JsonView({ View.Customers.class })
	@JoinColumn(name = "user_id")
	private User user;

	@Transient
	private Long userId;

	@OneToMany(mappedBy = "customer")
	@JsonView({ View.CustomersById.class })
	private List<Order> orders = new ArrayList<>();

	@ManyToMany
	@JsonView({ View.CustomersById.class })
	@JoinTable(name = "tb_library", joinColumns = @JoinColumn(name = "customer_id"), inverseJoinColumns = @JoinColumn(name = "product_id"))
	private Set<Product> library = new HashSet<>();

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
		Customer other = (Customer) obj;
		return Objects.equals(id, other.id);
	}

	public Customer() {

	}

	public Customer(Long id, String name, String phone, String creditScore) {
		super();
		this.id = id;
		this.name = name;
		this.phone = phone;
		this.creditScore = creditScore;
	}

	public Customer(Long id, String name, String phone, String creditScore, User user) {
		super();
		this.id = id;
		this.name = name;
		this.phone = phone;
		this.creditScore = creditScore;
		this.user = user;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public List<Order> getOrders() {
		return orders;
	}

	public String getCreditScore() {
		return creditScore;
	}

	public void setCreditScore(String creditScore) {
		this.creditScore = creditScore;
	}

	public Set<Product> getLibrary() {
		return library;
	}

	public void setLibrary(Set<Product> library) {
		this.library = library;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

}
