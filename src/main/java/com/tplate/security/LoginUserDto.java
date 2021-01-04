package com.tplate.security;

import lombok.Data;

@Data
public class LoginUserDto {
    private String accessToken;
    private String username;
    private String name;
    private String lastname;
    private String email;
    private String telefono;

    public LoginUserDto(String username, String accessToken,String name, String lastname, String email, String telefono) {
        this.accessToken = accessToken;
        this.username = username;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.telefono = telefono;
    }
}
