package com.chequier.gestion.back.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chequier.gestion.back.model.Historique;
import com.chequier.gestion.back.model.User;
import com.chequier.gestion.back.repository.HistoriqueRepository;
import com.chequier.gestion.back.repository.UserRepository;

@RestController
@RequestMapping("/historiques")
@CrossOrigin
public class HistoriqueController {

    private final HistoriqueRepository historiqueRepository;
    private final UserRepository userRepository;

    public HistoriqueController(HistoriqueRepository historiqueRepository, UserRepository userRepository) {
        this.historiqueRepository = historiqueRepository;
        this.userRepository = userRepository;
    }

    // Voir mes historiques (client connecté)
    @GetMapping("/mes")
    public ResponseEntity<?> mesHistoriques(Authentication auth) {
        User me = userRepository.findByEmail(auth.getName()).orElse(null);
        if (me == null) return ResponseEntity.status(401).body(Map.of("error","Non autorisé"));

        List<Historique> list = historiqueRepository.findByUtilisateur(me);
        return ResponseEntity.ok(list);
    }

    // Voir tous les historiques (admin)
    @GetMapping("/all")
    public List<Historique> allHistoriques() {
        return historiqueRepository.findAll();
    }
}
