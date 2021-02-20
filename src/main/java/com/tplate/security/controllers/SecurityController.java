package com.tplate.security.controllers;

import com.tplate.security.services.SecurityService;
import com.tplate.security.dtos.CredentialDto;
import com.tplate.security.dtos.NewUserDto;
import com.tplate.security.dtos.ResetPasswordDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/security")
public class SecurityController {

    @Autowired
    SecurityService securityService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody CredentialDto credentialDto){
        return this.securityService.loguear(credentialDto);
    }

    //Recupero de password
    @PostMapping("/reset-password")
    public ResponseEntity resetPassword(@RequestBody(required=true) ResetPasswordDto resetPasswordDto){
        return this.securityService.resetPassword(resetPasswordDto);
    }

    @PostMapping("/sign-up")
    public ResponseEntity signUp(@RequestBody(required=true) NewUserDto newUserDto){
        return this.securityService.signUp(newUserDto);
    }

}
