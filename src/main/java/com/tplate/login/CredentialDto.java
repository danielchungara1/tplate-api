package com.tplate.login;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CredentialDto {
    private String username;
    private String password;
}
