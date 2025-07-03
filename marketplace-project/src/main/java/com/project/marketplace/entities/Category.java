package com.project.marketplace.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonView;
import com.project.marketplace.view.View;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
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
@Table(name = "tb_category")
public class Category implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	@JsonView({ View.Categories.class, View.Products.class })
	private Long id;

	@JsonView({ View.Categories.class, View.Products.class, View.CustomersById.class })
	private String name;

	@ManyToMany(mappedBy = "categories")
	@JsonView({ View.CategoriesById.class })
	private Set<Product> products = new HashSet<>();


	public Category(Long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	
}
