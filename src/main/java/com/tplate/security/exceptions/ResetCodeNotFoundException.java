package com.tplate.security.exceptions;

public class ResetCodeNotFoundException extends Exception {

    public ResetCodeNotFoundException(String s) {
        super(s);
    }

    public ResetCodeNotFoundException() {
        super("Code not found.");
    }


}
