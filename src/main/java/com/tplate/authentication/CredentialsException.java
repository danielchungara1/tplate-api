package com.tplate.authentication;

public class CredentialsException extends Exception {
    public CredentialsException(String s) {
        super(s);
    }

    public CredentialsException() {
        super("Credenciales invalidas.");
    }
}
