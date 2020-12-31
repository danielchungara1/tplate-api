package com.tplate.security;

public class CredentialDtoException extends Exception {
    public CredentialDtoException(String s) {
        super(s);
    }

    public CredentialDtoException() {
        super("Credenciales invalidas.");
    }
}
