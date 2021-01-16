package com.tplate.security;

import com.tplate.security.exceptions.BaseDtoException;
import com.tplate.security.exceptions.ResetPasswordDtoException;
import com.tplate.security.jwt.JwtTokenUtil;
import com.tplate.user.User;
import com.tplate.user.UserRepository;
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

    @Autowired
    EmailSenderService emailSenderService;


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
            User user = this.userRepository.findByUsername(credentialDto.getUsername()).get();
            String name = user.getName();
            String lastname = user.getLastname();
            String email = user.getEmail();
            String telefono = user.getTelefono();
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

    @Transactional
    public ResponseEntity resetPassword(ResetPasswordDto resetPasswordDto) {

        try {

            //Validacion
            this.validateDto(resetPasswordDto);
            this.validateEmail(resetPasswordDto.getEmail());

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
            return new ResponseEntity("Se envio correctamente el mail para resetear el password.", HttpStatus.OK);

        } catch (ResetPasswordDtoException | BaseDtoException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            log.error("Error inesperado. {}, {}", e.getMessage(), e.getClass().getCanonicalName());
            return new ResponseEntity("Error al loguear al usuario.", HttpStatus.CONFLICT);
        }
    }

    private String tokenFrom(String email) {
        throw new UnsupportedOperationException();
    }

    private void validateEmail(String email) throws BaseDtoException {
        if (!this.userRepository.existsByEmail(email)){
            log.error("Email inexistente, Se esta intentado resetear un password con un mail invalido. {}", email);
            throw new BaseDtoException("Email inexistente.");
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

    private void validateDto(ResetPasswordDto resetPasswordDto) throws ResetPasswordDtoException {
        try {
            Objects.requireNonNull(resetPasswordDto);
            Objects.requireNonNull(resetPasswordDto.getEmail());
        } catch (NullPointerException e) {
            log.error("Reset Password DTO invalido. {}", resetPasswordDto);
            throw new ResetPasswordDtoException("El email es requerido.");
        }

    }
}
