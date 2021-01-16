package com.tplate.security;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ResetPasswordDto {
    private String email;
}
