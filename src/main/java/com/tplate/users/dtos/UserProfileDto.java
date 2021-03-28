package com.tplate.users.dtos;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserProfileDto {

    private String name;
    private String lastname;
    private String email;
    private String phone;

}
