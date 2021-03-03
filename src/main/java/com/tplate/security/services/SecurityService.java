package com.tplate.security.services;

import com.tplate.exceptions.ValidatorException;
import com.tplate.responses.builders.ResponseBuilder;
import com.tplate.rol.RolRepository;
import com.tplate.security.dtos.*;
import com.tplate.security.email.Email;
import com.tplate.security.email.EmailSenderService;
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
    EmailSenderService emailSenderService;

    @Autowired
    RolRepository rolRepository;

    @Autowired
    ResetCodeRepository resetCodeRepository;

    //Miscelaneos
    private Random rand = new Random();

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
    public ResponseEntity resetPasswordStep1(ResetPasswordStep1Dto resetPasswordDto) {
        try {
            // Validacion Dto
            resetPasswordDto.validate();
            String email = resetPasswordDto.getEmail();

            // Validacion acoplada a la DB
            if (!this.userRepository.existsByUsername(email)) {
                log.error("Email inexistente, Se esta intentado resetear un password con un mail invalido. {}", email);
                throw new BaseDtoException("Email inexistente.");
            }

            // Generar y guardar el reset code
            ResetPassword resetPassword = this.resetCodeRepository
                    .findByUser(this.userRepository.findByUsername(email).get())
                    .orElse(null);
            if (resetPassword == null) {
                resetPassword = ResetPassword.builder()
                        .code(String.format("%04d", this.rand.nextInt(10000)))
                        .expiration(new Date(System.currentTimeMillis() + (2 * 60 * 1000)))
                        .user(this.userRepository.findByUsername(email).get())
                        .build();
            } else {
                resetPassword.setCode(String.format("%04d", this.rand.nextInt(10000)));
                resetPassword.setExpiration(new Date(System.currentTimeMillis() + (2 * 60 * 1000)));
            }

            this.resetCodeRepository.save(resetPassword);

            //Envio del mail
            this.emailSenderService.send(Email.builder()
                    .to(email)
                    .resetCode(resetPassword.getCode())
                    .subject("Reset Password Code")
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

    @Transactional
    public ResponseEntity resetPasswordStep2(ResetPasswordStep2Dto resetPasswordDto) {
        try {
            // Validacion Dto
            resetPasswordDto.validate();
            String email = resetPasswordDto.getEmail();

            // Validacion acoplada a la DB
            if (!this.userRepository.existsByUsername(email)) {
                log.error("Email inexistente, Se esta intentado resetear un password con un mail invalido. {}", email);
                throw new BaseDtoException("Email inexistente.");
            }

            // Validacion del codigo de reseto
            ResetPassword resetPassword = this.resetCodeRepository
                    .findByUser(this.userRepository.findByUsername(email).get())
                    .orElse(null);
            if (resetPassword == null) {
                log.error("El email {}, no tiene asociado ningun codigo de reseto de password.", email);
                throw new BaseDtoException("Codigo de reseteo inexistente.");
            }

            // Validacion de coincidencia del codigo
            if (!resetPassword.getCode().equals(resetPasswordDto.getCode())) {
                log.error("El codigo de reseteo en la base {}, no coincide con el suministrado {}."
                        , resetPassword.getCode(), resetPasswordDto.getCode());
                throw new BaseDtoException("Codigo de reseto no coincide con el enviado. " + resetPasswordDto.getCode());
            }

            // Validacion de expiracion del codigo
            if (! (new Date(System.currentTimeMillis()).before(resetPassword.getExpiration()))) {
                log.error("El codigo de reseteo expiro {}.",resetPassword.getCode());
                throw new BaseDtoException("Codigo de reseto expiro.");
            }

            // Persistencia del nuevo password
            User user = this.userRepository.findByUsername(resetPasswordDto.getEmail()).get();
            user.setPassword(passwordEncoder.encode(resetPasswordDto.getPassword()));
            userRepository.save(user);
            log.info("Reseto de password exitoso. Usuario {}", user.getUsername());

            return ResponseBuilder.builder()
                    .ok()
                    .message("Se modifico el password correctamente.")
                    .build();
        } catch (ValidatorException | BaseDtoException e) {
            return ResponseBuilder.buildConflict(e.getMessage());

        } catch (Exception e) {
            log.error("Error inesperado. {}, {}", e.getMessage(), e.getClass().getCanonicalName());
            return ResponseBuilder.buildConflict("Error inesperado. Consulte a Sistemas.");
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

        } catch (SignInException | ValidatorException e) {
            return ResponseBuilder.buildConflict(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado. {}, {}", e.getMessage(), e.getClass().getCanonicalName());
            return ResponseBuilder.buildConflict("Error al registrar el usuario.");
        }
    }
}
