package com.tplate.user;

import com.tplate.rol.Rol;
import javafx.scene.canvas.GraphicsContext;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "USERS")
@Data
public class User {

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
    private Rol rol;

    @Column(name = "NAME")
    private String name;

    @Column(name = "LASTNAME")
    private String lastname;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "TELEFONO")
    private String telefono;

    @Column(name = "RESET_PASSWORD_TOKEN")
    private String resetPassToken ;

}
