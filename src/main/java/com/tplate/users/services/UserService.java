package com.tplate.users.services;

import com.tplate.exceptions.UserNotFoundException;
import com.tplate.builders.ResponseEntityBuilder;
import com.tplate.users.dtos.UserProfileDto;
import com.tplate.users.models.User;
import com.tplate.users.repositories.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Transactional
    public ResponseEntity editProfile(UserProfileDto userProfileDto, Long idUser) {

        try {

            // Validate existence
            this.userRepository.findById(idUser).orElseThrow(UserNotFoundException::new);

            // Save profile
            User user = this.userRepository.getOne(idUser);
            user.setName(userProfileDto.getName());
            user.setLastname(userProfileDto.getLastname());
            user.setPhone(userProfileDto.getPhone());
            user.setEmail(userProfileDto.getEmail());
            User user_ = this.userRepository.save(user);

            // Response
            return ResponseEntityBuilder.builder()
                    .ok()
                    .dto(user_, UserProfileDto.class)
                    .message("Profile edited.")
                    .build();
        } catch (UserNotFoundException e) {
            log.error("User not found. {}", e.getMessage());
            return ResponseEntityBuilder.buildBadRequest(e.getMessage());

        } catch (Exception e) {
            log.error("Something went wrong. {}, {}", e.getMessage(), e.getClass().getCanonicalName());
            return ResponseEntityBuilder.buildSomethingWrong(e.getMessage());
        }
    }

}
