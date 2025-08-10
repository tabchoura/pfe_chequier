package com.chequier.gestion.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chequier.gestion.model.Role;
import com.chequier.gestion.model.User;
import com.chequier.gestion.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserRepository userRepository, PasswordEncoder encoder,
                          AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.authenticationManager = authenticationManager;
    }

    // REGISTER
   @PostMapping("/register")
public ResponseEntity<?> register(@RequestBody @Valid RegisterBody body) {
    if (userRepository.existsByEmail(body.email)) {
        return ResponseEntity.badRequest().body(Map.of("error", "Email déjà utilisé"));
    }
    Role role = body.role == null ? Role.CLIENT : body.role;
    User u = new User(body.nom, body.prenom, body.email, encoder.encode(body.password), role);
    userRepository.save(u);

    // Génération d'un token simple (UUID)
    String token = java.util.UUID.randomUUID().toString();

    // Retour JSON complet
    return ResponseEntity.ok(Map.of(
            "id", u.getId(),
            "nom", u.getNom(),
            "prenom", u.getPrenom(),
            "email", u.getEmail(),
            "role", u.getRole().name(),
            "token", token
    ));
}

    // LOGIN (vérifie juste les credentials)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginBody body) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(body.email, body.password)
        );
        userRepository.findByEmail(body.email).orElseThrow();
        return ResponseEntity.ok(Map.of(
                "message", "Identifiants valides"
               
        ));
    }

    // LOGOUT (vide session/contexte si présent)
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        // si jamais une session existe (utile si tu passes plus tard en sessions stateful)
        if (request.getSession(false) != null) {
            request.getSession(false).invalidate();
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(Map.of("message", "Déconnecté (côté client, supprime les identifiants Basic)"));
    }

    // Bodies inline
    public static class RegisterBody {
        @NotBlank public String nom;
        @NotBlank public String prenom;
        @Email @NotBlank public String email;
        @NotBlank public String password;
        public Role role; // ADMIN | CLIENT | AGENT_BANCAIRE
    }

    public static class LoginBody {
        @Email @NotBlank public String email;
        @NotBlank public String password;
    }
}
