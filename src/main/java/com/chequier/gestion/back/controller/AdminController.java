package com.chequier.gestion.back.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chequier.gestion.back.model.Role;
import com.chequier.gestion.back.model.StatutChequier;
import com.chequier.gestion.back.model.StatutDemande;
import com.chequier.gestion.back.repository.ChequierRepository;
import com.chequier.gestion.back.repository.DemandeRepository;
import com.chequier.gestion.back.repository.UserRepository;

@RestController
@RequestMapping("/admin")
@CrossOrigin
public class AdminController {

    private final UserRepository userRepository;
    private final DemandeRepository demandeRepository;
    private final ChequierRepository chequierRepository;

    public AdminController(UserRepository userRepository,
                           DemandeRepository demandeRepository,
                           ChequierRepository chequierRepository) {
        this.userRepository = userRepository;
        this.demandeRepository = demandeRepository;
        this.chequierRepository = chequierRepository;
    }

    // Dashboard: réservé ADMIN (tu peux élargir à AGENT_BANCAIRE si tu veux)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/dashboard")
    public ResponseEntity<?> dashboard() {

        long usersTotal = userRepository.count();
        long admins = userRepository.countByRole(Role.ADMIN);
        long agents = userRepository.countByRole(Role.AGENT_BANCAIRE);
        long clients = userRepository.countByRole(Role.CLIENT);

        long demandesTotal = demandeRepository.count();
        long dAttente = demandeRepository.countByStatut(StatutDemande.EN_ATTENTE);
        long dValidees = demandeRepository.countByStatut(StatutDemande.VALIDEE);
        long dRejetees = demandeRepository.countByStatut(StatutDemande.REJETEE);

        long chequiersTotal = chequierRepository.count();
        long cActifs = chequierRepository.countByStatut(StatutChequier.ACTIF);
        long cBloques = chequierRepository.countByStatut(StatutChequier.BLOQUE);
        long cClotures = chequierRepository.countByStatut(StatutChequier.CLOTURE);

        return ResponseEntity.ok(Map.of(
                "users", Map.of(
                        "total", usersTotal,
                        "admins", admins,
                        "agents", agents,
                        "clients", clients
                ),
                "demandes", Map.of(
                        "total", demandesTotal,
                        "en_attente", dAttente,
                        "validees", dValidees,
                        "rejetees", dRejetees
                ),
                "chequiers", Map.of(
                        "total", chequiersTotal,
                        "actifs", cActifs,
                        "bloques", cBloques,
                        "clotures", cClotures
                )
        ));
    }
}
