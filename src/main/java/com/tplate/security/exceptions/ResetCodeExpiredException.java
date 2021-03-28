package com.tplate.security.exceptions;

public class ResetCodeExpiredException extends Exception {

    public ResetCodeExpiredException(String s) {
        super(s);
    }

    public ResetCodeExpiredException() {
        super("Code has expired.");
    }


}
