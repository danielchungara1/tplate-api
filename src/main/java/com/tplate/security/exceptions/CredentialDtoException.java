package com.tplate.security.exceptions;

public class CredentialDtoException extends Exception {
    public CredentialDtoException(String s) {
        super(s);
    }

    public CredentialDtoException() {
        super("Credenciales invalidas.");
    }
}
