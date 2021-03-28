package com.tplate.security.exceptions;

public class ResetCodeNotMatchingException extends Exception {

    public ResetCodeNotMatchingException(String s) {
        super(s);
    }

    public ResetCodeNotMatchingException() {
        super("Code is incorrect.");
    }


}
