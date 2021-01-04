package com.tplate.security;

import com.tplate.security.jwt.JwtTokenUtil;
import com.tplate.user.UserEntity;
import com.tplate.user.UserRepository;
import com.google.common.base.Strings;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tplate.security.exceptions.CredentialDtoException;
import java.util.Objects;

@Service
@Log4j2
public class LoginService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Transactional
    public ResponseEntity loguear(CredentialDto credentialDto) {

        try {

            //Validacion campos no nulos
            this.validateDto(credentialDto);

            //Verificar que el usuario exista en la BD y coincida el password
            this.authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            credentialDto.getUsername(), credentialDto.getPassword()
                    )
            );

            //Generar token
            String username = credentialDto.getUsername();
            String token = this.jwtTokenUtil.generateToken(this.userRepository.findByUsername(credentialDto.getUsername()).get());
            UserEntity userEntity = this.userRepository.findByUsername(credentialDto.getUsername()).get();
            String name = userEntity.getName();
            String lastname = userEntity.getLastname();
            String email = userEntity.getEmail();
            String telefono = userEntity.getTelefono();
            log.info("Usuario logueado OK. {}", credentialDto.getUsername());
            return new ResponseEntity(new LoginUserDto(username, token, name, lastname, email, telefono), HttpStatus.OK);

        } catch (CredentialDtoException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.CONFLICT);

        } catch (AuthenticationException e) {
            return new ResponseEntity("Usuario o password incorrectos.", HttpStatus.CONFLICT);

        } catch (Exception e) {
            log.error("Error inesperado. {}, {}", e.getMessage(), e.getClass().getCanonicalName());
            return new ResponseEntity("Error al loguear al usuario.", HttpStatus.CONFLICT);
        }
    }

    private void validateDto(CredentialDto credentialDto) throws CredentialDtoException {
        try {
            Objects.requireNonNull(credentialDto);
            Objects.requireNonNull(credentialDto.getUsername());
            Objects.requireNonNull(credentialDto.getPassword());
        } catch (NullPointerException e) {
            log.error("Credential DTO invalido. {}", credentialDto);
            throw new CredentialDtoException("Username y Password son requeridos.");
        }

    }
}
