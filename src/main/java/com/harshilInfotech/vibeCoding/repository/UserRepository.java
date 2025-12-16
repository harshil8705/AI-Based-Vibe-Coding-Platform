package com.harshilInfotech.vibeCoding.repository;

import com.harshilInfotech.vibeCoding.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(@Email(message = "Please Entier the valid Email") @NotBlank String username);
}