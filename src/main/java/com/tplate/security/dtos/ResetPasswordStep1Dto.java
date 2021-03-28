package com.tplate.security.dtos;

import com.tplate.exceptions.FormValidatorException;
import com.tplate.validators.FormValidator;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ResetPasswordStep1Dto {
    private String email;

    public void validate() throws FormValidatorException {
        FormValidator.evaluate()
                .isRequired(this.email, "Email")
                .isEmail(this.email, "Email");
    }
}
