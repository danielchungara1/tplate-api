package com.tplate.security.dtos;

import com.tplate.exceptions.ValidatorException;
import com.tplate.validators.Validator;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ResetPasswordDto {
    private String email;

    public void validate() throws ValidatorException {
        Validator.evaluate()
                .isRequired(this.email, "Email");
    }
}
