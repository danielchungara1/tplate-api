package com.tplate.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {

    boolean existsByUsernameAndPassword(String username, String password);

    boolean existsByUsername(String username);
}
