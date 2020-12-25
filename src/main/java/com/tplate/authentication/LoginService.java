package com.tplate.authentication;

import com.tplate.user.UserEntity;
import com.tplate.user.UserRepository;
import com.google.common.base.Strings;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Log4j2
public class LoginService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JWTAuthenticationService jwtAuthenticationService;

    public ResponseEntity loguear(CredentialDto credentialDto) {
        try {
            this.validateCredentials(credentialDto);
            String token = this.jwtAuthenticationService.generateAccessToken(credentialDto.getUsername());
            log.info("Login exitoso. {}", credentialDto.getUsername() );
            return new ResponseEntity(token, HttpStatus.OK);
        }catch (CredentialsException e){
            log.error("Error en las credenciales. {}", e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatus.CONFLICT);
        }catch (Exception e){
            log.error("Error inesperado. {}, {}", e.getMessage(), e.getClass().getCanonicalName());
            return new ResponseEntity("Error inesperado.", HttpStatus.CONFLICT);
        }
    }

    private void validateCredentials(CredentialDto credentialDto) throws CredentialsException{
        if (Objects.isNull(credentialDto) || Strings.isNullOrEmpty(credentialDto.getUsername()) || Strings.isNullOrEmpty(credentialDto.getPassword())){
            throw new CredentialsException("El username y password son requeridos.");
        }

        if (!userRepository.existsByUsername(credentialDto.getUsername())){
            throw new CredentialsException();
        }else{
            UserEntity userEntity = userRepository.getOne(credentialDto.getUsername());
            if (!passwordEncoder.matches(credentialDto.getPassword(), userEntity.getPassword())){
                throw new CredentialsException();
            }
        }
    }
}
