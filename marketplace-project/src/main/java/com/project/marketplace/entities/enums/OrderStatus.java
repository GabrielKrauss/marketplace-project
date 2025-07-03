package com.project.marketplace.entities.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum OrderStatus {

	@JsonProperty("WAITING_PAYMENT") WAITING_PAYMENT(1),
	@JsonProperty("PAID") PAID(2),
	@JsonProperty("SHIPPED") SHIPPED(3),
	@JsonProperty("DELIVERED") DELIVERED(4),
	@JsonProperty("CANCELED") CANCELED(5);

	private int code;

	private OrderStatus(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public static OrderStatus valueOf(int code) {
		for (OrderStatus value : OrderStatus.values()) {
			if (value.getCode() == code) {
				return value;
			}
		}
		throw new IllegalArgumentException("Invalid OrderStatus code!");
	}

}
