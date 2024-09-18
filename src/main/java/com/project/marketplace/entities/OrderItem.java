package com.project.marketplace.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonView;
import com.project.marketplace.entities.pk.OrderItemPK;
import com.project.marketplace.view.View;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "tb_order_item")
public class OrderItem implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private OrderItemPK id = new OrderItemPK();

	@JsonView({ View.OrdersById.class, View.Products.class })
	private Integer quantity;

	@Transient
	private Long productId;

	public OrderItem() {
	}

	public OrderItem(Order order, Product product, Integer quantity) {
		super();
		id.setOrder(order);
		id.setProduct(product);
		this.quantity = quantity;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
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
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductsId(Long productId) {
		this.productId = productId;
	}

	@JsonView({ View.OrdersById.class })
	public Double getSubTotal() {
		return id.getProduct().getUnitPrice() * quantity;
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
		OrderItem other = (OrderItem) obj;
		return Objects.equals(id, other.id);
	}

}
