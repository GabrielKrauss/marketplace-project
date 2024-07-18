package com.project.marketplace.entities.enums;

public enum AddressType {

	HOMEADDRESS(1), BUSINESSADDRESS(2), SHIPPINGADDRESS(3);

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
