package com.tplate.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/login")
public class LoginController {

    @Autowired
    LoginService loginService;

    @PostMapping("")
    public ResponseEntity login(@RequestBody(required=false) CredentialDto credentialDto){
        return this.loginService.loguear(credentialDto);
    }

    //Recupero de password
    @PostMapping("/reset-password")
    public ResponseEntity resetPassword(@RequestBody(required=true) ResetPasswordDto resetPasswordDto){
        return this.loginService.resetPassword(resetPasswordDto);
    }

}
