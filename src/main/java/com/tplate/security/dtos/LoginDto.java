package com.tplate.security.dtos;

import com.tplate.exceptions.ValidatorException;
import com.tplate.validators.Validator;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class LoginDto {

    private String username;
    private String password;

    public void validate() throws ValidatorException {
        Validator.evaluate()
            .isRequired(this.username, "Correo")
            .isRequired(this.password, "Contraseña")
            .isEmail(this.username, "Correo");
    }
}
