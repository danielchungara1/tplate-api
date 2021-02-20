package com.tplate.validators;

import com.tplate.exceptions.ValidatorException;

public class Validator {

    public static Validator evaluate() {
        return new Validator();
    }

    public Validator isRequired(String value, String name) throws ValidatorException {

        if (value == null || value.equals("") || value.trim().length() <= 0) {
            throw new ValidatorException(name + " es requerido.");
        } else {
            return this;
        }
    }

    public Validator isEmail(String value, String name) throws ValidatorException {
        if (value == null) {
            throw new ValidatorException(name + " no tiene formato de email.");
        } else {
            return this;
        }
    }
}
