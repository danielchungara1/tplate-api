package com.tplate.security.models;

import com.tplate.users.models.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "RESET_PASSWORD")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResetPassword {

    @Id
    @Column(name = "ID")
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    private User user;

    @Column(name = "CODE")
    private String code;

    @Column(name = "EXPIRATION")
    private Date expiration;

}
