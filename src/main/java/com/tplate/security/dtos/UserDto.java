package com.tplate.security.dtos;

import lombok.Data;

@Data
public class UserDto {
    private String token;
    private String username;
    private String name;
    private String lastname;
    private String email;
    private String telefono;
}
