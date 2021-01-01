package com.tplate.user;

import com.tplate.exceptions.IdInexistenteException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Log4j2
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Transactional
    public ResponseEntity editProfile(UserProfileDto userProfileDto, Long idUser) {

        try {
            //Validacion campos no nulos
            this.validateDto(userProfileDto);

            //Validar existencia de usuario
            this.validateExistenceById(idUser);

            //Guardar Perfil
            UserEntity userEntity = this.userRepository.getOne(idUser);
            userEntity.setName(userProfileDto.getName());
            userEntity.setLastname(userProfileDto.getLastname());
            userEntity.setEmail(userProfileDto.getEmail());
            userEntity.setTelefono(userProfileDto.getTelefono());
            this.userRepository.save(userEntity);

            return new ResponseEntity("Perfil editado.", HttpStatus.OK);

        } catch (UserProfileDtoException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.CONFLICT);

        } catch (IdInexistenteException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.CONFLICT);

        } catch (Exception e) {
            log.error("Error inesperado. {}, {}", e.getMessage(), e.getClass().getCanonicalName());
            return new ResponseEntity("Error al editar el perfil.", HttpStatus.CONFLICT);
        }
    }

    private void validateExistenceById(Long id) throws IdInexistenteException {
        try {
            this.userRepository.findById(id).orElseThrow(() -> new IdInexistenteException(id) );
        } catch (IdInexistenteException e) {
            log.error("Usuario inexistente. ID: {}", id);
            throw e;
        }
    }

    private void validateDto(UserProfileDto userProfileDto) throws UserProfileDtoException {
        try {
            Objects.requireNonNull(userProfileDto);
        } catch (NullPointerException e) {
            log.error("User Profile DTO invalido. {}", userProfileDto);
            throw new UserProfileDtoException("No se recibio el nuevo perfil.");
        }

    }


}
