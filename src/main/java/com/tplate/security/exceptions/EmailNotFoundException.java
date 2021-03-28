package com.tplate.security.exceptions;

public class EmailNotFoundException extends Exception {

    public EmailNotFoundException(String s) {
        super(s);
    }

    public EmailNotFoundException() {
        super("Email not found.");
    }


}
