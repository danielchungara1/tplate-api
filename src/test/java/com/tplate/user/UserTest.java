package com.tplate.user;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Log4j2
class UserTest {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    void generarHashingPassword() {
       log.info(passwordEncoder.encode("terricola"));
    }
}