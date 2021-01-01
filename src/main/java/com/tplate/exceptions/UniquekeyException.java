package com.tplate.exceptions;

public class UniquekeyException extends Exception {

	private static final long serialVersionUID = -2477800973478400861L;
	
	private String field;
	private String code;
	
	public UniquekeyException(String message, String field, String code) {
		super(message);
		
		this.field = field;
		this.code = code;
	}

	public UniquekeyException(Exception e) {
		super(e);
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
