package com.chequier.gestion.controller;

import com.chequier.gestion.model.Historique;
import com.chequier.gestion.model.User;
import com.chequier.gestion.repository.HistoriqueRepository;
import com.chequier.gestion.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
