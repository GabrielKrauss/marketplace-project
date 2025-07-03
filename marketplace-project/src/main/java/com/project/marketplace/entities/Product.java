package com.project.marketplace.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
@Table(name = "tb_product")
public class Product implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	@JsonView({ View.Products.class, View.CategoriesById.class, View.Orders.class, View.CustomersById.class })
	private Long id;

	@JsonView({ View.Products.class, View.Categories.class, View.OrdersById.class, View.CustomersById.class })
	private String name;

	@JsonView({ View.Products.class, View.CategoriesById.class })
	private String description;

	@JsonView({ View.Products.class, View.Categories.class })
	private Double unitPrice;
	
	@JsonView({ View.Products.class, View.Categories.class })
	private Integer stock;

	@JsonView({ View.Products.class, View.Categories.class })
	private Boolean sellIndicator;

	@JsonView({ View.Products.class, View.Categories.class })
	private Boolean isPhysical;

	@JsonView({ View.Products.class })
	private Boolean isDeleted;

	@JsonView({ View.Products.class, View.CustomersById.class})
	private List<String> imagesUrl = new ArrayList<>();
	
	@JsonView({ View.Products.class, View.CustomersById.class})
	private String fileUrl;
	

	@ManyToMany
	@JoinTable(name = "tb_product_category", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
	@JsonView({ View.Products.class, View.CustomersById.class})
	private Set<Category> categories = new HashSet<>();

	@OneToMany(mappedBy = "id.product")
	private Set<OrderItem> items = new HashSet<>();

	@ManyToMany(mappedBy = "library")
	private Set<Customer> customers = new HashSet<>();

	@Transient
	private Set<Long> categoriesId = new HashSet<>();

	public Product(Long id, String name, String description, Double unitPrice, Integer stock, Boolean sellIndicator,
			Boolean isPhysical, List<String> imagesUrl, String fileUrl) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.unitPrice = unitPrice;
		this.stock = stock;
		this.sellIndicator = sellIndicator;
		this.isPhysical = isPhysical;
		this.isDeleted = false;
		this.imagesUrl = imagesUrl;
		this.fileUrl = fileUrl;
	}
	
	public Product(Long id, String name, String description, Double unitPrice, Boolean sellIndicator,
			Boolean isPhysical, List<String> imagesUrl, String fileUrl) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.unitPrice = unitPrice;
		this.sellIndicator = sellIndicator;
		this.isPhysical = isPhysical;
		this.isDeleted = false;
		this.imagesUrl = imagesUrl;
		this.fileUrl = fileUrl;
	}

	public Set<Order> getOrders() {
		Set<Order> set = new HashSet<>();
		for (OrderItem orderItem : items) {
			set.add(orderItem.getOrder());
		}
		return set;
	}

}
