package com.tplate.exceptions;

public class IdInexistenteException extends Exception {

    public IdInexistenteException(String s) {
        super(s);
    }

    public IdInexistenteException(Long id) {
        super("Id Inexistente. " + id);
    }
}
