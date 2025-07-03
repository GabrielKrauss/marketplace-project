package com.project.marketplace.services.exceptions;

public class ResourceNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public ResourceNotFoundException(String message) {
		super(message);
	}
	
	public ResourceNotFoundException(Long id) {
		super("Resource Not Found. Id: " + id);
	}
	

}
