package com.project.marketplace.entities.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum AddressType {

	@JsonProperty("HOME_ADDRESS") HOME_ADDRESS(1),
	@JsonProperty("BUSINES_ADDRESS") BUSINES_ADDRESS(2),
	@JsonProperty("SHIPPING_ADDRESS") SHIPPING_ADDRESS(3);

	private int code;

	private AddressType(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public static AddressType valueOf(int code) {
		for (AddressType value : AddressType.values()) {
			if (value.getCode() == code) {
				return value;
			}
		}
		throw new IllegalArgumentException("Invalid Address Type code!");
	}

}
