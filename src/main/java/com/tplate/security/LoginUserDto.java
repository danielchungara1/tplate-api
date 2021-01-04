package com.tplate.security;

import lombok.Data;

@Data
public class LoginUserDto {
    private String accessToken;
    private String username;

    public LoginUserDto(String username, String accessToken) {
        this.accessToken = accessToken;
        this.username = username;
    }
}
