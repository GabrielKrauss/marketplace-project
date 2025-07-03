package com.project.marketplace.entities;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonView;
import com.project.marketplace.entities.pk.OrderItemPK;
import com.project.marketplace.view.View;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
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
@Table(name = "tb_order_item")
public class OrderItem implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	@EqualsAndHashCode.Include
	private OrderItemPK id = new OrderItemPK();

	@JsonView({ View.Orders.class, View.Products.class })
	private Integer quantity;

	@Transient
	@JsonView({ View.Orders.class})
	private Long productId;

	public OrderItem(Order order, Product product, Integer quantity) {
		super();
		id.setOrder(order);
		id.setProduct(product);
		this.quantity = quantity;
		this.productId = product.getId();
		
	}

	public Order getOrder() {
		return id.getOrder();
	}

	public void setOrder(Order order) {
		id.setOrder(order);
	}

	@JsonView({ View.OrdersById.class })
	public Product getProduct() {
		return id.getProduct();
	}

	public void setProduct(Product product) {
	    id.setProduct(product);
	    this.productId = product.getId();
	}

	@JsonView({ View.OrdersById.class })
	public Double getSubTotal() {
		return id.getProduct().getUnitPrice() * quantity;
	}
	
	@JsonView({ View.Orders.class })
	public Long getProductId() {
		 if (id == null) {
		        throw new IllegalStateException("ID is null");
		    }
		    if (id.getProduct() == null) {
		        return this.productId;
		    }
		    return id.getProduct().getId();
	}
	
}
