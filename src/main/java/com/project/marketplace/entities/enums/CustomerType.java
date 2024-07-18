package com.project.marketplace.entities.enums;

public enum CustomerType {

	LEGALPERSON(1), NATURALPERSON(2);

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
