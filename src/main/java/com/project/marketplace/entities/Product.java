package com.project.marketplace.entities;

import java.io.Serializable;
import java.util.HashSet;
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
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_product")
public class Product implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonView({ View.Products.class, View.CategoriesById.class, View.OrdersById.class, View.CustomersById.class })
	private Long id;

	@JsonView({ View.Products.class, View.Categories.class, View.OrdersById.class, View.CustomersById.class })
	private String name;

	@JsonView({ View.Products.class, View.CategoriesById.class })
	private String description;

	@JsonView({ View.Products.class, View.Categories.class })
	private Double unitPrice;

	@JsonView({ View.Products.class, View.Categories.class })
	private Boolean sellIndicator;

	@JsonView({ View.Products.class, View.Categories.class })
	private Boolean isPhysical;

	@ManyToMany
	@JoinTable(name = "tb_product_category", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
	@JsonView({ View.Products.class })
	private Set<Category> categories = new HashSet<>();

	@OneToMany(mappedBy = "id.product")
	private Set<OrderItem> items = new HashSet<>();

	@ManyToMany(mappedBy = "library")
	private Set<Customer> customers = new HashSet<>();

	public Product() {
	}

	public Product(Long id, String name, String description, Double unitPrice, Boolean sellIndicator,
			Boolean isPhysical) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.unitPrice = unitPrice;
		this.sellIndicator = sellIndicator;
		this.isPhysical = isPhysical;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(Double unitPrice) {
		this.unitPrice = unitPrice;
	}

	public Boolean getSellIndicator() {
		return sellIndicator;
	}

	public void setSellIndicator(Boolean sellIndicator) {
		this.sellIndicator = sellIndicator;
	}

	public Boolean getIsPhysical() {
		return isPhysical;
	}

	public void setIsPhysical(Boolean isPhysical) {
		this.isPhysical = isPhysical;
	}

	public Set<Category> getCategories() {
		return categories;
	}

	public Set<Order> getOrders() {
		Set<Order> set = new HashSet<>();
		for (OrderItem x : items) {
			set.add(x.getOrder());
		}
		return set;
	}

	public void setCategories(Set<Category> categories) {
		this.categories = categories;
	}

	public Set<Customer> getCustomers() {
		return customers;
	}

	public void setCustomers(Set<Customer> customers) {
		this.customers = customers;
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
		Product other = (Product) obj;
		return Objects.equals(id, other.id);
	}
}
