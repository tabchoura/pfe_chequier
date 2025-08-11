package com.chequier.gestion.back.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chequier.gestion.back.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    long countByRole(com.chequier.gestion.back.model.Role role);

}
