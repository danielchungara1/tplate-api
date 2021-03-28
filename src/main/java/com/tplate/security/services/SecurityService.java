package com.tplate.security.services;

// External Dependencies
import com.google.common.collect.ImmutableMap;
import com.tplate.security.exceptions.*;
import lombok.extern.log4j.Log4j2;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Internal Dependencies
import com.tplate.exceptions.FormValidatorException;
import com.tplate.builders.ResponseEntityBuilder;
import com.tplate.security.rol.RolRepository;
import com.tplate.security.dtos.*;
import com.tplate.security.email.Email;
import com.tplate.security.email.EmailService;
import com.tplate.security.jwt.JwtTokenUtil;
import com.tplate.security.models.ResetPassword;
import com.tplate.security.repositories.ResetCodeRepository;
import com.tplate.users.models.User;
import com.tplate.users.repositories.UserRepository;

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
    EmailService emailService;

    @Autowired
    RolRepository rolRepository;

    @Autowired
    ResetCodeRepository resetCodeRepository;

    @Autowired
    ResetPasswordService resetPasswordService;

    @Transactional
    public ResponseEntity loguear(LoginDto loginDto) {

        try {
            // Validate Dto
            loginDto.validate();

            // Credentials Validation
            this.authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getUsername(), loginDto.getPassword()
                    )
            );

            // Generate token
            String token = this.jwtTokenUtil.generateToken(this.userRepository.findByUsername(loginDto.getUsername()).get());
            User user = this.userRepository.findByUsername(loginDto.getUsername()).get();
            user.setToken(token);
            log.info("User logged OK. {}", loginDto.getUsername());

            //Response
            return ResponseEntityBuilder
                    .builder()
                    .ok()
                    .message("Session started successfully.")
                    .dto(user, UserLoggedDto.class)
                    .build();

        } catch (AuthenticationException e) {
            return ResponseEntityBuilder.buildConflict("Invalid Credentials.");

        } catch (FormValidatorException e) {
            return ResponseEntityBuilder.buildConflict(e.getMessage());

        } catch (Exception e) {
            log.error("Unexpected Error. {}, {}", e.getMessage(), e.getClass().getCanonicalName());
            return ResponseEntityBuilder.buildSomethingWrong();
        }
    }

    @Transactional
    public ResponseEntity resetPasswordStep1(ResetPasswordStep1Dto resetPasswordDto) {

        try {

            // Dto Validation
            resetPasswordDto.validate();
            String email = resetPasswordDto.getEmail();

            // Existence Validation
            if (!this.userRepository.existsByUsername(email)) {
                log.error("Email not found. {}", email);
                throw new EmailNotFoundException();
            }

            // Generate Reset Code
            ResetPassword resetPassword = this.resetCodeRepository
                    .findByUser(this.userRepository.findByUsername(email).get())
                    .orElse(null);
            // If not exists reset code then I create it.
            if (resetPassword == null) {
                resetPassword = ResetPassword.builder()
                        .code(this.resetPasswordService.code())
                        .expiration(this.resetPasswordService.expiration())
                        .user(this.userRepository.findByUsername(email).get())
                        .build();
            } else { // If already exists then only I update it.
                resetPassword.setCode(this.resetPasswordService.code());
                resetPassword.setExpiration(this.resetPasswordService.expiration());
            }
            this.resetCodeRepository.save(resetPassword);

            // Send email
            this.emailService.send(Email.builder()
                    .to(email)
                    .subject("Reset Code for change password.")
                    .data(ImmutableMap.<String, Object>builder()
                            .put("resetCode", resetPassword.getCode())
                            .build()
                    )
                    .build());

            log.info("Email with the reset code was sent successfully. {}", email);

            return ResponseEntityBuilder.builder()
                    .ok()
                    .message("Email was sent successfully.")
                    .build();

        } catch (FormValidatorException | EmailNotFoundException e) {
            return ResponseEntityBuilder.buildConflict(e.getMessage());

        } catch (MailSendException e) {
            log.error("Email wasn't sent. {}, {}", e.getMessage(), e.getClass().getCanonicalName());
            return ResponseEntityBuilder.buildConflict(e.getMessage());

        } catch (Exception e) {
            log.error("Unexpected Error. {}, {}", e.getMessage(), e.getClass().getCanonicalName());
            return ResponseEntityBuilder.buildSomethingWrong();
        }
    }

    @Transactional
    public ResponseEntity resetPasswordStep2(ResetPasswordStep2Dto resetPasswordDto) {
        try {
            // Dto Validation
            resetPasswordDto.validate();
            String email = resetPasswordDto.getEmail();

            // Validate existence
            if (!this.userRepository.existsByUsername(email)) {
                log.error("Email not exist. {}", email);
                throw new EmailNotFoundException();
            }

            // Validate reset code
            ResetPassword resetPassword = this.resetCodeRepository
                    .findByUser(this.userRepository.findByUsername(email).get())
                    .orElse(null);
            if (resetPassword == null) {
                log.error("Reset code not found for email {}", email);
                throw new ResetCodeNotFoundException();
            }

            // Validate matching
            if (!resetPassword.getCode().equals(resetPasswordDto.getCode())) {
                log.error("Codes not matching {} {}."
                        , resetPassword.getCode(), resetPasswordDto.getCode());
                throw new ResetCodeNotMatchingException();
            }

            // Validate expiration
            if (!(new Date(System.currentTimeMillis()).before(resetPassword.getExpiration()))) {
                log.error("Code has expired {}.", resetPassword.getCode());
                throw new ResetCodeExpiredException();
            }

            // Save new password
            User user = this.userRepository.findByUsername(resetPasswordDto.getEmail()).get();
            user.setPassword(passwordEncoder.encode(resetPasswordDto.getPassword()));
            userRepository.save(user);
            log.info("The new password has been saved. User {}", user.getUsername());

            return ResponseEntityBuilder.builder()
                    .ok()
                    .message("The password has been changed.")
                    .build();
        } catch ( FormValidatorException | ResetCodeNotFoundException
                | EmailNotFoundException | ResetCodeNotMatchingException
                | ResetCodeExpiredException e ) {
            return ResponseEntityBuilder.buildConflict(e.getMessage());

        } catch (Exception e) {
            log.error("Something was wrong. {}, {}", e.getMessage(), e.getClass().getCanonicalName());
            return ResponseEntityBuilder.buildSomethingWrong();
        }
    }

    @Transactional
    public ResponseEntity signUp(SingUpDto singUpDto) {

        try {

            // Validate dto
            singUpDto.validate();

            // Validate username existence
            if (this.userRepository.existsByUsername(singUpDto.getUsername())) {
                log.error("Username exists. {}", singUpDto.getUsername());
                throw new UsernameExistsException();
            }

            // Save new user
            User newUser = User.builder()
                    .username(singUpDto.getUsername())
                    .password(this.passwordEncoder.encode(singUpDto.getPassword()))
                    .rol(this.rolRepository.findByName("ADMINISTRADOR").get())
                    .build();
            this.userRepository.save(newUser);

            // Response
            log.info("User was registered. {}", singUpDto.getUsername());
            return ResponseEntityBuilder.builder()
                    .ok()
                    .message("User was registered.  " + newUser.getUsername())
                    .build();

        } catch (FormValidatorException | UsernameExistsException e) {
            return ResponseEntityBuilder.buildConflict(e.getMessage());
        } catch (Exception e) {
            log.error("Something went wrong. {}, {}", e.getMessage(), e.getClass().getCanonicalName());
            return ResponseEntityBuilder.buildSomethingWrong();
        }
    }
}
