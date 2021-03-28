package com.tplate.security.dtos;

import com.tplate.exceptions.FormValidatorException;
import com.tplate.validators.FormValidator;
import lombok.Data;

@Data
public class SingUpDto {
    private String username;
    private String password;

    public void validate() throws FormValidatorException {
        FormValidator.evaluate()
                .isRequired(this.username, "Email")
                .isRequired(this.password, "Password")
                .isEmail(this.username, "Email");
    }
}
