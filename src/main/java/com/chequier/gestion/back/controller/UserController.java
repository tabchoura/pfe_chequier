package com.chequier.gestion.back.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chequier.gestion.back.model.User;
import com.chequier.gestion.back.repository.UserRepository;

@RestController
@RequestMapping("/users")
@CrossOrigin
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) { this.userRepository = userRepository; }

    // Profil de l'utilisateur connecté (HTTP Basic requis)
    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication auth) {
        User me = userRepository.findByEmail(auth.getName()).orElse(null);
        if (me == null) return ResponseEntity.status(401).body(Map.of("error","Non autorisé"));

        return ResponseEntity.ok(Map.of(
                "id", me.getId(),
                "nom", me.getNom(),
                "prenom", me.getPrenom(),
                "email", me.getEmail(),
                "role", me.getRole().name()
        ));
    }
}
