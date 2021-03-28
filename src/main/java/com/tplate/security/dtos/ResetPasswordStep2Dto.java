package com.tplate.security.dtos;

import com.tplate.exceptions.FormValidatorException;
import com.tplate.validators.FormValidator;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ResetPasswordStep2Dto {
    private String email;
    private String code;
    private String password;

    public void validate() throws FormValidatorException {
        FormValidator.evaluate()
                .isRequired(this.email, "Email")
                .isRequired(this.code, "Code")
                .isRequired(this.password, "Password")
                .isEmail(this.email, "Email");
    }
}
