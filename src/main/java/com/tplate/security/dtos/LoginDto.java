package com.tplate.security.dtos;

import com.tplate.exceptions.FormValidatorException;
import com.tplate.validators.FormValidator;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class LoginDto {

    private String username;
    private String password;

    public void validate() throws FormValidatorException {
        FormValidator.evaluate()
            .isRequired(this.username, "Username")
            .isRequired(this.password, "Password");
    }
}
