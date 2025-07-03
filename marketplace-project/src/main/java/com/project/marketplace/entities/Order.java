package com.project.marketplace.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import com.project.marketplace.entities.enums.OrderStatus;
import com.project.marketplace.view.View;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "tb_order")
public class Order implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	@JsonView({ View.Orders.class, View.Customers.class, View.Products.class })
	private Long id;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT")
	@JsonView({ View.Orders.class, View.Customers.class, View.Products.class })
	private Instant moment;

	@JsonView({ View.Orders.class, View.Customers.class, View.Products.class })
	private Integer orderStatus;

	@JsonView({ View.Orders.class, View.Customers.class })
	private Boolean isDeleted;

	@ManyToOne
	@JoinColumn(name = "address_id")
	@JsonView({ View.Orders.class, View.Customers.class })
	private Address deliveryAddress;

	@ManyToOne
	@JoinColumn(name = "coupon_id")
	@JsonView({ View.Orders.class })
	private Coupon coupon;

	@Transient
	private Long addressId;

	@Transient
	private Long customerId;

	@ManyToOne
	@JoinColumn(name = "customer_id")
	@JsonView({ View.Orders.class, View.Products.class })
	private Customer customer;

	@OneToMany(mappedBy = "id.order")
	@JsonView({ View.Orders.class })
	private List<OrderItem> items = new ArrayList<>();

	@JsonView({ View.Orders.class, View.Customers.class, View.Products.class })
	private Double total;

	public Order(Long id, OrderStatus orderStatus, Customer customer, Address deliveryAddress, Coupon coupon) {
		super();
		this.id = id;
		this.moment = Instant.now();
		setOrderStatus(orderStatus);
		this.customer = customer;
		this.customerId = customer.getId();
		this.deliveryAddress = deliveryAddress;
		this.coupon = coupon;
		this.setTotal(this.getTotal());
		this.isDeleted = false;
	}

	public OrderStatus getOrderStatus() {
		return OrderStatus.valueOf(orderStatus);
	}

	public void setOrderStatus(OrderStatus orderStatus) {
		if (orderStatus != null) {
			this.orderStatus = orderStatus.getCode();
		}
	}

	public Double getTotal() {
		if(getOrderStatus() == OrderStatus.WAITING_PAYMENT) {
			double sum = 0.0;
			for (OrderItem orderItem : items) {
				sum += orderItem.getSubTotal();
			}
			if (coupon != null && coupon.getIsActive()) {
				if (coupon.getDiscountValue() != 0.0) {
					sum -= coupon.getDiscountValue();
				}
				if (coupon.getDiscountPercentage() != 0.0) {
					sum -= sum * (coupon.getDiscountPercentage() / 100);
				}
			}
			sum = sum > 0 ? sum : 0.0;
			BigDecimal formattedSum = new BigDecimal(sum).setScale(2, RoundingMode.HALF_UP);
			return formattedSum.doubleValue();
		}
		return total;
		
	}


}
