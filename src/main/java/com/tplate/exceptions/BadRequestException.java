package com.tplate.exceptions;

public class BadRequestException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1505156032530889837L;
	
	private String field;
	private String code;
	
	public BadRequestException(String message, String field, String code) {
		super(message);
		
		this.field = field;
		this.code = code;
	}

	public BadRequestException(Exception e) {
		super(e);
	}
	
	public BadRequestException(String string) {
		super(string);
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
