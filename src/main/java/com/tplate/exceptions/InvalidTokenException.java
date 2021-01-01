package com.tplate.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class InvalidTokenException extends RuntimeException {

	public InvalidTokenException(String string) {
		super(string);
	}

	public InvalidTokenException(Exception e) {
		super(e);
	}

	private static final long serialVersionUID = -2821544327647604892L;

}