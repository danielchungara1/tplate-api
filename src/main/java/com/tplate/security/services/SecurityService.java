package com.tplate.security.services;

import com.tplate.exceptions.ValidatorException;
import com.tplate.responses.builders.ResponseBuilder;
import com.tplate.rol.RolRepository;
import com.tplate.security.dtos.CredentialDto;
import com.tplate.security.dtos.NewUserDto;
import com.tplate.security.dtos.ResetPasswordDto;
import com.tplate.security.dtos.UserDto;
import com.tplate.security.email.Email;
import com.tplate.security.email.EmailSenderService;
import com.tplate.security.exceptions.BaseDtoException;
import com.tplate.security.exceptions.SignInException;
import com.tplate.security.jwt.JwtTokenUtil;
import com.tplate.user.User;
import com.tplate.user.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
public class SecurityService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    EmailSenderService emailSenderService;

    @Autowired
    RolRepository rolRepository;


    @Transactional
    public ResponseEntity loguear(CredentialDto credentialDto) {

        try {

            //Validate Dto
            credentialDto.validate();

            //Verificar que el usuario exista en la BD y coincida el password
            this.authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            credentialDto.getUsername(), credentialDto.getPassword()
                    )
            );

            //Generar token
            String token = this.jwtTokenUtil.generateToken(this.userRepository.findByUsername(credentialDto.getUsername()).get());
            User user = this.userRepository.findByUsername(credentialDto.getUsername()).get();
            user.setToken(token);
            log.info("Usuario logueado OK. {}", credentialDto.getUsername());

            //Response
            return ResponseBuilder
                    .builder()
                    .ok()
                    .message("Usuario logueado correctamente")
                    .dto(user, UserDto.class)
                    .build();

        } catch (AuthenticationException e) {
            return ResponseBuilder.buildConflict("Usuario o password incorrectos.");
        } catch (ValidatorException e) {
            return ResponseBuilder.buildConflict(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado. {}, {}", e.getMessage(), e.getClass().getCanonicalName());
            return ResponseBuilder.buildConflict("Error al loguear al usuario.");
        }
    }

    @Transactional
    public ResponseEntity resetPassword(ResetPasswordDto resetPasswordDto) {

        try {

            //Validacion
            resetPasswordDto.validate();
            this.validateEmailDB(resetPasswordDto.getEmail());

            String email = resetPasswordDto.getEmail();

            //Generar token
            String resetToken = this.tokenFrom(email);
            User user = this.userRepository.findByEmail(email).get();
            user.setResetPassToken(resetToken);
            this.userRepository.save(user);

            //Envio del mail
            this.emailSenderService.send(Email.builder()
                    .to(email)
                    .token(resetToken)
                    .build());

            log.info("Envio de email para resetear el password OK. {}", email);


            return ResponseBuilder.builder()
                    .ok()
                    .message("Se envio correctamente el mail para resetear el password.")
                    .build();
        } catch (ValidatorException | BaseDtoException e) {
            return ResponseBuilder.buildConflict(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado. {}, {}", e.getMessage(), e.getClass().getCanonicalName());
            return ResponseBuilder.buildConflict("Error inesperado. Consulte a Sistemas.");
        }
    }

    private String tokenFrom(String email) {
        throw new UnsupportedOperationException();
    }

    private void validateEmailDB(String email) throws BaseDtoException {
        if (!this.userRepository.existsByEmail(email)) {
            log.error("Email inexistente, Se esta intentado resetear un password con un mail invalido. {}", email);
            throw new BaseDtoException("Email inexistente.");
        }
    }

    @Transactional
    public ResponseEntity signUp(NewUserDto newUserDto) {

        try {

            //Validacion campos no nulos
            newUserDto.validate();

            //Verificar que el username no exista
            if (this.userRepository.existsByUsername(newUserDto.getUsername())) {
                log.error("Username existente. {}", newUserDto.getUsername());
                throw new SignInException("Username existente.");
            }
            //Crear nuevo usuario
            User newUser = User.builder()
                    .username(newUserDto.getUsername())
                    .password(this.passwordEncoder.encode(newUserDto.getPassword()))
                    .rol(this.rolRepository.findByName("ADMINISTRADOR").get())
                    .build();
            this.userRepository.save(newUser);

            //Response
            log.info("Usuario registrado OK. {}", newUserDto.getUsername());
            return ResponseBuilder.builder()
                    .ok()
                    .message("Usuario registrado correctamente. " + newUser.getUsername())
                    .build();

        } catch (SignInException e) {
            return ResponseBuilder.buildConflict(e.getMessage());

        } catch (Exception e) {
            log.error("Error inesperado. {}, {}", e.getMessage(), e.getClass().getCanonicalName());
            return ResponseBuilder.buildConflict("Error al registrar el usuario.");
        }
    }
}
