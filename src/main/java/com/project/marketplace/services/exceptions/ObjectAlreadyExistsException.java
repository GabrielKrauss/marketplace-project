package com.project.marketplace.services.exceptions;

public class ObjectAlreadyExistsException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ObjectAlreadyExistsException(String object) {
		super(object + " already exists.");
	}
}
