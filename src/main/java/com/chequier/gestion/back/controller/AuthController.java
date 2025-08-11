package com.chequier.gestion.back.controller;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.chequier.gestion.back.model.Role;
import com.chequier.gestion.back.model.User;
import com.chequier.gestion.back.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
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

        // token "visuel" pour l'UI (HTTP Basic reste la sécurité effective)
        String token = java.util.UUID.randomUUID().toString();

        return ResponseEntity.ok(Map.of(
                "id", u.getId(),
                "nom", u.getNom(),
                "prenom", u.getPrenom(),
                "email", u.getEmail(),
                "role", u.getRole().name(),
                "token", token
        ));
    }

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginBody body) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(body.email, body.password)
        );
        var u = userRepository.findByEmail(body.email).orElseThrow();
        String token = java.util.UUID.randomUUID().toString(); // même logique que register
        return ResponseEntity.ok(Map.of(
                "id", u.getId(),
                "nom", u.getNom(),
                "prenom", u.getPrenom(),
                "email", u.getEmail(),
                "role", u.getRole().name(),
                "token", token
        ));
    }

    // LOGOUT
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        if (request.getSession(false) != null) request.getSession(false).invalidate();
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(Map.of("message", "Déconnecté"));
    }

    // Bodies
    public static class RegisterBody {
        @NotBlank public String nom;
        @NotBlank public String prenom;
        @Email @NotBlank public String email;
        @NotBlank public String password;
        public Role role; // CLIENT | AGENT
    }
    public static class LoginBody {
        @Email @NotBlank public String email;
        @NotBlank public String password;
    }
}
