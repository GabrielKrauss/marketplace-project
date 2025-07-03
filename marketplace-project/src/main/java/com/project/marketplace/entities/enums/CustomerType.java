package com.project.marketplace.entities.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum CustomerType {

	@JsonProperty("LEGAL_PERSON") LEGAL_PERSON(1), 
	@JsonProperty("NATURAL_PERSON") NATURAL_PERSON(2);

	private int code;

	private CustomerType(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public static CustomerType valueOf(int code) {
		for (CustomerType value : CustomerType.values()) {
			if (value.getCode() == code) {
				return value;
			}
		}
		throw new IllegalArgumentException("Invalid Customer Type code!");
	}
}
