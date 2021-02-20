package com.tplate.security.exceptions;

public class SignInException extends Exception {
    public SignInException(String s) {
        super(s);
    }

    public SignInException() {
        super("Datos de usuario invalidos.");
    }
}
