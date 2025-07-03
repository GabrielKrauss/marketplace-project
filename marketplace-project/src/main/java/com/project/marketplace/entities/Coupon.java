package com.project.marketplace.entities;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonView;
import com.project.marketplace.view.View;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "tb_coupon")
public class Coupon implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	@JsonView({ View.Orders.class, View.Coupons.class })
	private Long id;

	@JsonView({ View.Orders.class, View.Coupons.class })
	private String code;
	
	@JsonView({ View.Orders.class, View.Coupons.class })
    private Double discountValue;

	@JsonView({ View.Orders.class, View.Coupons.class })
	private Double discountPercentage;

	@JsonView({ View.Orders.class, View.Coupons.class })
    private Boolean isActive;

}
