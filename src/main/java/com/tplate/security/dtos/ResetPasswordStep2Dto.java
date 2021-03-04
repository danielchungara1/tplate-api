package com.tplate.security.dtos;

import com.tplate.exceptions.ValidatorException;
import com.tplate.validators.Validator;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ResetPasswordStep2Dto {
    private String email;
    private String code;
    private String password;

    public void validate() throws ValidatorException {
        Validator.evaluate()
                .isRequired(this.email, "Email")
                .isRequired(this.code, "Codigo de Recuperacion")
                .isRequired(this.password, "Password")
                .isEmail(this.email, "Email");
    }
}
