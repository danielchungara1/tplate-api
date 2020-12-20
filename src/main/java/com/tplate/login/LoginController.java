package com.tplate.login;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/login")
@CrossOrigin
@Data
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping
    public ResponseEntity login(@RequestBody(required=false) CredentialDto credentialDto){
        return this.loginService.loguear(credentialDto);
    }

}
