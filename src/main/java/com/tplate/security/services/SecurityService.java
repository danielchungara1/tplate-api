package com.tplate.security.services;

import com.google.common.collect.ImmutableMap;
import com.tplate.exceptions.ValidatorException;
import com.tplate.responses.builders.ResponseEntityBuilder;
import com.tplate.security.rol.RolRepository;
import com.tplate.security.dtos.*;
import com.tplate.security.email.Email;
import com.tplate.security.email.EmailService;
import com.tplate.security.exceptions.BaseDtoException;
import com.tplate.security.exceptions.SignInException;
import com.tplate.security.jwt.JwtTokenUtil;
import com.tplate.security.models.ResetPassword;
import com.tplate.security.repositories.ResetCodeRepository;
import com.tplate.user.User;
import com.tplate.user.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Random;

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
    EmailService emailServiceService;

    @Autowired
    RolRepository rolRepository;

    @Autowired
    ResetCodeRepository resetCodeRepository;

    @Autowired
    ResetPasswordService resetPasswordService;

    //Miscelaneos
    private Random rand = new Random();

    @Transactional
    public ResponseEntity loguear(LoginDto loginDto) {

        try {
            //Validate Dto
            loginDto.validate();

            //Verificar que el usuario exista en la BD y coincida el password
            this.authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getUsername(), loginDto.getPassword()
                    )
            );

            //Generar token
            String token = this.jwtTokenUtil.generateToken(this.userRepository.findByUsername(loginDto.getUsername()).get());
            User user = this.userRepository.findByUsername(loginDto.getUsername()).get();
            user.setToken(token);
            log.info("Usuario logueado OK. {}", loginDto.getUsername());

            //Response
            return ResponseEntityBuilder
                    .builder()
                    .statusCode__ok()
                    .message("Session started successfully.")
                    .dto(user, UserDto.class)
                    .build();

        } catch (AuthenticationException e) {
            return ResponseEntityBuilder.buildConflict("Correo o contraseña incorrectos.");
        } catch (ValidatorException e) {
            return ResponseEntityBuilder.buildConflict(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado. {}, {}", e.getMessage(), e.getClass().getCanonicalName());
            return ResponseEntityBuilder.buildConflict("Error al iniciar sesión.");
        }
    }

    @Transactional
    public ResponseEntity resetPasswordStep1(ResetPasswordStep1Dto resetPasswordDto) {
        try {
            // Validacion Dto
            resetPasswordDto.validate();
            String email = resetPasswordDto.getEmail();

            // Validacion acoplada a la DB
            if (!this.userRepository.existsByUsername(email)) {
                log.error("Email inexistente, Se esta intentado resetear un password con un mail invalido. {}", email);
                throw new BaseDtoException("El correo electrónico no existe.");
            }

            // Generar y guardar el reset code
            ResetPassword resetPassword = this.resetCodeRepository
                    .findByUser(this.userRepository.findByUsername(email).get())
                    .orElse(null);
            if (resetPassword == null) {
                resetPassword = ResetPassword.builder()
                        .code(this.resetPasswordService.code())
                        .expiration(this.resetPasswordService.expiration())
                        .user(this.userRepository.findByUsername(email).get())
                        .build();
            } else {
                resetPassword.setCode(this.resetPasswordService.code());
                resetPassword.setExpiration(this.resetPasswordService.expiration());
            }

            this.resetCodeRepository.save(resetPassword);

            //Envio del mail
            this.emailServiceService.send(Email.builder()
                    .to(email)
                    .subject("Código para cambio de contraseña.")
                    .data(ImmutableMap.<String, Object>builder()
                            .put("resetCode", resetPassword.getCode())
                            .build()
                    )
                    .build());

            log.info("Envio de email para resetear el password OK. {}", email);

            return ResponseEntityBuilder.builder()
                    .statusCode__ok()
                    .message("Se envió el código para el cambio de contraseña.")
                    .build();
        } catch (ValidatorException | BaseDtoException e) {
            return ResponseEntityBuilder.buildConflict(e.getMessage());
        } catch (MailSendException e) {
            log.error("Error envio de email. {}, {}", e.getMessage(), e.getClass().getCanonicalName());
            return ResponseEntityBuilder.buildConflict("No se puede enviar el código en estos momentos.");
        } catch (Exception e) {
            log.error("Error inesperado. {}, {}", e.getMessage(), e.getClass().getCanonicalName());
            return ResponseEntityBuilder.buildConflict("Error inesperado.");
        }
    }

    @Transactional
    public ResponseEntity resetPasswordStep2(ResetPasswordStep2Dto resetPasswordDto) {
        try {
            // Validacion Dto
            resetPasswordDto.validate();
            String email = resetPasswordDto.getEmail();

            // Validacion acoplada a la DB
            if (!this.userRepository.existsByUsername(email)) {
                log.error("Email inexistente, Se esta intentado resetear un password con un mail invalido. {}", email);
                throw new BaseDtoException("Correo electrónico no existe.");
            }

            // Validacion del codigo de reseto
            ResetPassword resetPassword = this.resetCodeRepository
                    .findByUser(this.userRepository.findByUsername(email).get())
                    .orElse(null);
            if (resetPassword == null) {
                log.error("El email {}, no tiene asociado ningun codigo de reseto de password.", email);
                throw new BaseDtoException("Código no existente.");
            }

            // Validacion de coincidencia del codigo
            if (!resetPassword.getCode().equals(resetPasswordDto.getCode())) {
                log.error("El codigo de reseteo en la base {}, no coincide con el suministrado {}."
                        , resetPassword.getCode(), resetPasswordDto.getCode());
                throw new BaseDtoException("Código incorrecto. ");
            }

            // Validacion de expiracion del codigo
            if (!(new Date(System.currentTimeMillis()).before(resetPassword.getExpiration()))) {
                log.error("El codigo de reseteo expiro {}.", resetPassword.getCode());
                throw new BaseDtoException("El código ha expirado.");
            }

            // Persistencia del nuevo password
            User user = this.userRepository.findByUsername(resetPasswordDto.getEmail()).get();
            user.setPassword(passwordEncoder.encode(resetPasswordDto.getPassword()));
            userRepository.save(user);
            log.info("Reseto de password exitoso. Usuario {}", user.getUsername());

            return ResponseEntityBuilder.builder()
                    .statusCode__ok()
                    .message("Se modifico la contraseña.")
                    .build();
        } catch (ValidatorException | BaseDtoException e) {
            return ResponseEntityBuilder.buildConflict(e.getMessage());

        } catch (Exception e) {
            log.error("Error inesperado. {}, {}", e.getMessage(), e.getClass().getCanonicalName());
            return ResponseEntityBuilder.buildConflict("Error inesperado.");
        }
    }

    @Transactional
    public ResponseEntity signUp(SingUpDto singUpDto) {

        try {

            //Validacion campos no nulos
            singUpDto.validate();

            //Verificar que el username no exista
            if (this.userRepository.existsByUsername(singUpDto.getUsername())) {
                log.error("Username existente. {}", singUpDto.getUsername());
                throw new SignInException("Correo electrónico existente.");
            }
            //Crear nuevo usuario
            User newUser = User.builder()
                    .username(singUpDto.getUsername())
                    .password(this.passwordEncoder.encode(singUpDto.getPassword()))
                    .rol(this.rolRepository.findByName("ADMINISTRADOR").get())
                    .build();
            this.userRepository.save(newUser);

            //Response
            log.info("Usuario registrado OK. {}", singUpDto.getUsername());
            return ResponseEntityBuilder.builder()
                    .statusCode__ok()
                    .message("Usuario registrado.  " + newUser.getUsername())
                    .build();

        } catch (SignInException | ValidatorException e) {
            return ResponseEntityBuilder.buildConflict(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado. {}, {}", e.getMessage(), e.getClass().getCanonicalName());
            return ResponseEntityBuilder.buildConflict("Error al registrar usuario.");
        }
    }
}
