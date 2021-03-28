package com.tplate.security.dtos;

import lombok.Data;

@Data
public class UserLoggedDto {
    private String token;
    private String username;
    private String name;
    private String lastname;
    private String email;
    private String phone;
}
