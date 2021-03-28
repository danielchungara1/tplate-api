package com.tplate.users.controllers;

import com.tplate.users.dtos.UserProfileDto;
import com.tplate.users.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping(value = "/profile/edit/{idUser}")
    @PreAuthorize("hasAuthority('EDITAR_PERFIL')")
    public ResponseEntity editProfile(@RequestBody(required=true) UserProfileDto userProfileDto,
                                @PathVariable Long idUser){
        return this.userService.editProfile(userProfileDto, idUser);
    }

}
