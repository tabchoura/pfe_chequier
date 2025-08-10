package com.chequier.gestion.controller;

import com.chequier.gestion.model.Chequier;
import com.chequier.gestion.model.Historique;
import com.chequier.gestion.model.StatutChequier;
import com.chequier.gestion.model.User;
import com.chequier.gestion.repository.ChequierRepository;
import com.chequier.gestion.repository.HistoriqueRepository;
import com.chequier.gestion.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chequiers")
@CrossOrigin
public class ChequierController {

    private final ChequierRepository chequierRepository;
    private final UserRepository userRepository;
    private final HistoriqueRepository historiqueRepository;

    public ChequierController(ChequierRepository chequierRepository,
                              UserRepository userRepository,
                              HistoriqueRepository historiqueRepository) {
        this.chequierRepository = chequierRepository;
        this.userRepository = userRepository;
        this.historiqueRepository = historiqueRepository;
    }

    // Sanity check
    @GetMapping("/_ping")
    public Map<String, String> ping() { return Map.of("status", "ok"); }

    // === CLIENT: lister MES chéquiers
    @GetMapping("/mes")
    public ResponseEntity<?> mesChequiers(Authentication auth) {
        User me = userRepository.findByEmail(auth.getName()).orElse(null);
        if (me == null) return ResponseEntity.status(401).body(Map.of("error", "Non autorisé"));
        List<Chequier> list = chequierRepository.findByClient(me);
        return ResponseEntity.ok(list);
    }

    // === STAFF: lister TOUS les chéquiers (ADMIN / AGENT_BANCAIRE)
    @PreAuthorize("hasAnyRole('ADMIN','AGENT_BANCAIRE')")
    @GetMapping
    public ResponseEntity<?> tous(Authentication auth) {
        // Auth déjà vérifiée par @PreAuthorize
        return ResponseEntity.ok(chequierRepository.findAll());
    }

    // === STAFF ou PROPRIÉTAIRE: consulter un chéquier par id
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id, Authentication auth) {
        User me = userRepository.findByEmail(auth.getName()).orElse(null);
        if (me == null) return ResponseEntity.status(401).body(Map.of("error", "Non autorisé"));

        Chequier ch = chequierRepository.findById(id).orElse(null);
        if (ch == null) return ResponseEntity.status(404).body(Map.of("error", "Chéquier introuvable"));

        // Le client ne peut voir que ses chéquiers ; le staff voit tout
        if (!isOwner(me, ch) && !isStaff(me)) {
            return ResponseEntity.status(403).body(Map.of("error", "Interdit"));
        }
        return ResponseEntity.ok(ch);
    }

    // === STAFF: changer le statut d'un chéquier (ACTIF/BLOQUE/CLOTURE)
    @PreAuthorize("hasAnyRole('ADMIN','AGENT_BANCAIRE')")
    @PutMapping("/{id}/statut")
    public ResponseEntity<?> changerStatut(@PathVariable Long id,
                                           @RequestBody Map<String, String> body,
                                           Authentication auth) {
        User me = userRepository.findByEmail(auth.getName()).orElse(null);
        if (me == null) return ResponseEntity.status(401).body(Map.of("error", "Non autorisé"));

        Chequier ch = chequierRepository.findById(id).orElse(null);
        if (ch == null) return ResponseEntity.status(404).body(Map.of("error", "Chéquier introuvable"));

        String statutStr = body.get("statut");
        if (statutStr == null || statutStr.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Champ 'statut' requis (ACTIF | BLOQUE | CLOTURE)"));
        }

        try {
            StatutChequier statut = StatutChequier.valueOf(statutStr);
            ch.setStatut(statut);
            chequierRepository.save(ch);

            // Historique
            historiqueRepository.save(new Historique(me,
                    "Changement du statut du chéquier ID " + ch.getId() + " en " + statut));

            return ResponseEntity.ok(Map.of("message", "Statut chéquier mis à jour"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Statut invalide (ACTIF | BLOQUE | CLOTURE)"));
        }
    }

    // === STAFF: supprimer un chéquier
    @PreAuthorize("hasAnyRole('ADMIN','AGENT_BANCAIRE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> supprimer(@PathVariable Long id, Authentication auth) {
        User me = userRepository.findByEmail(auth.getName()).orElse(null);
        if (me == null) return ResponseEntity.status(401).body(Map.of("error", "Non autorisé"));

        Chequier ch = chequierRepository.findById(id).orElse(null);
        if (ch == null) return ResponseEntity.status(404).body(Map.of("error", "Chéquier introuvable"));

        chequierRepository.delete(ch);

        // Historique
        historiqueRepository.save(new Historique(me, "Suppression du chéquier ID " + id));

        return ResponseEntity.ok(Map.of("message", "Chéquier supprimé"));
    }

    // Helpers
    private boolean isStaff(User u) {
        String r = (u.getRole() == null) ? "" : u.getRole().name();
        return r.equals("ADMIN") || r.equals("AGENT_BANCAIRE");
    }

    private boolean isOwner(User u, Chequier ch) {
        return ch.getClient() != null && ch.getClient().getId().equals(u.getId());
    }
}
