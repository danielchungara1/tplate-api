package com.tplate.user;

import com.tplate.rol.RolEntity;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "USERS")
@Data
public class UserEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long Id;

    @Column(name = "USERNAME")
    private String username;

    @Column(name = "PASSWORD")
    private String password;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="ROL_ID")
    private RolEntity rol;
}
