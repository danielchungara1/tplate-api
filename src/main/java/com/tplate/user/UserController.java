package com.tplate.user;

import com.tplate.authentication.CredentialDto;
import com.tplate.authentication.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @PostMapping(value = "/profile/edit")
    public ResponseEntity login(@RequestBody(required=false) CredentialDto credentialDto){
        return new ResponseEntity("Profile edit OK", HttpStatus.OK);
    }

}
