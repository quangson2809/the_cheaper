package com.example.the_cheaper.repository;

import com.example.the_cheaper.entity.PasswordResetTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetTokenEntity, Long> {

    Optional<PasswordResetTokenEntity> findByEmailAndOtpAndUsedFalse(String email, String otp);

    void deleteByEmail(String email);
}
