package com.tplate.security.dtos;

import com.tplate.exceptions.ValidatorException;
import com.tplate.validators.Validator;
import lombok.Data;

@Data
public class SingUpDto {
    private String username;
    private String password;

    public void validate() throws ValidatorException {
        Validator.evaluate()
                .isRequired(this.username, "Username")
                .isRequired(this.password, "Password")
                .isEmail(this.username, "Username");
    }
}
