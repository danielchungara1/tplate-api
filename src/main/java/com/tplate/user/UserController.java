package com.tplate.user;

import com.tplate.security.CredentialDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @PostMapping(value = "/profile/edit")
    @PreAuthorize("hasAuthority('EDITAR_PERFIL')")
    public ResponseEntity login(@RequestBody(required=false) CredentialDto credentialDto){
        return new ResponseEntity("Profile edit OK", HttpStatus.OK);
    }

}
