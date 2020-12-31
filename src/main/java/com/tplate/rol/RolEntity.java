package com.tplate.rol;

import com.tplate.permission.PermissionEntity;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "ROLES")
@Data
public class RolEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long Id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @ManyToMany(fetch= FetchType.EAGER)
    @JoinTable(
            name = "ROL_PERMISSIONS",
            joinColumns = @JoinColumn(
                    name = "ID_ROL", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(
                    name = "ID_PERMISSION", referencedColumnName = "ID"))
    private List<PermissionEntity> permissions;

}
