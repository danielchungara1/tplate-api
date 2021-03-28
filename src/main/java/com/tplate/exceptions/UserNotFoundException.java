package com.tplate.exceptions;

public class UserNotFoundException extends Exception {

    public UserNotFoundException(String s) {
        super(s);
    }

    public UserNotFoundException() {
        super("User not fournd.");
    }

    public UserNotFoundException(Long id) {
        super("User ID not found. " + id);
    }
}
