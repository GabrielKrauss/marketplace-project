package com.project.marketplace.entities.enums;

public enum Authorities {

	ADMIN(1), OPERATOR(2);

	private int code;

	private Authorities(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public static Authorities valueOf(int code) {
		for (Authorities value : Authorities.values()) {
			if (value.getCode() == code) {
				return value;
			}
		}
		throw new IllegalArgumentException("Invalid Authority code!");
	}
}
