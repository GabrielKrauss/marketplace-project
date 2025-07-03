package com.project.marketplace.services.exceptions;

public class ResourceNotAvailableException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ResourceNotAvailableException(Object id) {
		super("Product " + id + " is not available.");
	}

	public ResourceNotAvailableException(String message) {
		super(message);
	}

}
