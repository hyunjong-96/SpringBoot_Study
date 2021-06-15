package com.security.springbootsecurityjwt.common.customException;

public class InputNotFoundException extends RuntimeException{

	public InputNotFoundException(String message) {
		super(message);
	}
}
