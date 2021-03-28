package com.tplate.security.exceptions;

public class UsernameExistsException extends Exception {

    public UsernameExistsException(String s) {
        super(s);
    }

    public UsernameExistsException() {
        super("Username exists.");
    }


}
