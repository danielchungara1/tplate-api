package com.tplate.user;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

}
