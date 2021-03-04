package com.tplate.security.controllers;

import com.tplate.security.dtos.ResetPasswordStep1Dto;
import com.tplate.security.dtos.ResetPasswordStep2Dto;
import com.tplate.security.services.SecurityService;
import com.tplate.security.dtos.LoginDto;
import com.tplate.security.dtos.SingUpDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/security")
public class SecurityController {

    @Autowired
    SecurityService securityService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginDto loginDto){
        return this.securityService.loguear(loginDto);
    }

    //Recupero de password
    @PostMapping("/reset-password/step1")
    public ResponseEntity resetPassword(@RequestBody(required=true) ResetPasswordStep1Dto resetPasswordDto){
        return this.securityService.resetPasswordStep1(resetPasswordDto);
    }

    @PostMapping("/reset-password/step2")
    public ResponseEntity resetPassword(@RequestBody(required=true) ResetPasswordStep2Dto resetPasswordDto){
        return this.securityService.resetPasswordStep2(resetPasswordDto);
    }

    @PostMapping("/sign-up")
    public ResponseEntity signUp(@RequestBody(required=true) SingUpDto singUpDto){
        return this.securityService.signUp(singUpDto);
    }

}
