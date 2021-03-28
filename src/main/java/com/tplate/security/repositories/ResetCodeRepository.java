package com.tplate.security.repositories;

import com.tplate.security.models.ResetPassword;
import com.tplate.users.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResetCodeRepository extends JpaRepository<ResetPassword, Long> {
    Optional<ResetPassword> findByUser(User user);
}
