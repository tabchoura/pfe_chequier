package com.chequier.gestion.back.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chequier.gestion.back.model.Chequier;
import com.chequier.gestion.back.model.Demande;
import com.chequier.gestion.back.model.Historique;
import com.chequier.gestion.back.model.StatutDemande;
import com.chequier.gestion.back.model.TypeChequier;
import com.chequier.gestion.back.model.User;
import com.chequier.gestion.back.repository.ChequierRepository;
import com.chequier.gestion.back.repository.DemandeRepository;
import com.chequier.gestion.back.repository.HistoriqueRepository;
import com.chequier.gestion.back.repository.UserRepository;

@RestController
@RequestMapping("/demandes")
@CrossOrigin
public class DemandeController {

    private final DemandeRepository demandeRepository;
    private final UserRepository userRepository;
    private final ChequierRepository chequierRepository;
    private final HistoriqueRepository historiqueRepository; // ✅ ajouté

    public DemandeController(DemandeRepository demandeRepository,
                             UserRepository userRepository,
                             ChequierRepository chequierRepository,
                             HistoriqueRepository historiqueRepository) { // ✅ ajouté
        this.demandeRepository = demandeRepository;
        this.userRepository = userRepository;
        this.chequierRepository = chequierRepository;
        this.historiqueRepository = historiqueRepository; // ✅ ajouté
    }

    // Sanity check
    @GetMapping("/_ping")
    public Map<String,String> ping() { return Map.of("status","ok"); }

    // Créer une demande (auth obligatoire via Basic)
    @PostMapping
    public ResponseEntity<?> creerDemande(Authentication auth, @RequestBody DemandeBody body) {
        User client = userRepository.findByEmail(auth.getName()).orElse(null);
        if (client == null) return ResponseEntity.status(401).body(Map.of("error","Non autorisé"));

        Demande d = new Demande(client, body.typeChequier, body.commentaire);
        demandeRepository.save(d);

        // ✅ Enregistrement de l'historique
        historiqueRepository.save(new Historique(client, "Création de demande de chéquier"));

        return ResponseEntity.ok(Map.of(
            "message","Demande créée avec succès",
            "id", d.getId()
        ));
    }

    // Mes demandes (client)
    @GetMapping("/mes")
    public ResponseEntity<?> mesDemandes(Authentication auth) {
        User client = userRepository.findByEmail(auth.getName()).orElse(null);
        if (client == null) return ResponseEntity.status(401).body(Map.of("error","Non autorisé"));
        List<Demande> list = demandeRepository.findByClient(client);
        return ResponseEntity.ok(list);
    }

    // Modifier une demande (propriétaire OU admin/agent)
    @PutMapping("/{id}")
    public ResponseEntity<?> modifierDemande(@PathVariable Long id,
                                             Authentication auth,
                                             @RequestBody DemandeBody body) {
        User current = userRepository.findByEmail(auth.getName()).orElse(null);
        if (current == null) return ResponseEntity.status(401).body(Map.of("error","Non autorisé"));

        Demande d = demandeRepository.findById(id).orElse(null);
        if (d == null) return ResponseEntity.status(404).body(Map.of("error","Demande introuvable"));

        if (!isOwnerOrStaff(current, d)) {
            return ResponseEntity.status(403).body(Map.of("error","Interdit"));
        }

        if (body.typeChequier != null) d.setTypeChequier(body.typeChequier);
        if (body.commentaire != null) d.setCommentaire(body.commentaire);

        demandeRepository.save(d);
        return ResponseEntity.ok(Map.of("message","Demande modifiée"));
    }

    // Supprimer une demande (propriétaire OU admin/agent)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> supprimerDemande(@PathVariable Long id, Authentication auth) {
        User current = userRepository.findByEmail(auth.getName()).orElse(null);
        if (current == null) return ResponseEntity.status(401).body(Map.of("error","Non autorisé"));

        Demande d = demandeRepository.findById(id).orElse(null);
        if (d == null) return ResponseEntity.status(404).body(Map.of("error","Demande introuvable"));

        if (!isOwnerOrStaff(current, d)) {
            return ResponseEntity.status(403).body(Map.of("error","Interdit"));
        }

        demandeRepository.delete(d);
        return ResponseEntity.ok(Map.of("message","Demande supprimée"));
    }

    // ⚡ Changer le statut : si VALIDEE => créer automatiquement un chéquier
    @PutMapping("/{id}/statut")
    public ResponseEntity<?> changerStatut(@PathVariable Long id,
                                           @RequestBody Map<String, String> body,
                                           Authentication auth) {
        User current = userRepository.findByEmail(auth.getName()).orElse(null);
        if (current == null) return ResponseEntity.status(401).body(Map.of("error","Non autorisé"));

        String role = current.getRole() != null ? current.getRole().name() : "";
        if (!(role.equals("ADMIN") || role.equals("AGENT_BANCAIRE"))) {
            return ResponseEntity.status(403).body(Map.of("error","Interdit"));
        }

        Demande d = demandeRepository.findById(id).orElse(null);
        if (d == null) return ResponseEntity.status(404).body(Map.of("error","Demande introuvable"));

        String statutStr = body.get("statut");
        try {
            StatutDemande nouveau = StatutDemande.valueOf(statutStr);
            d.setStatut(nouveau);
            demandeRepository.save(d);

            if (nouveau == StatutDemande.VALIDEE) {
                int nbFeuillets = (d.getTypeChequier() == TypeChequier.CINQUANTE_FEUILLETS) ? 50 : 25;
                String numero = genererNumeroUnique();
                Chequier ch = new Chequier(numero, d.getClient(), d, nbFeuillets);
                chequierRepository.save(ch);

                // ✅ Historique de délivrance du chéquier
                historiqueRepository.save(new Historique(current, "Validation de la demande et création du chéquier"));

                return ResponseEntity.ok(Map.of(
                        "message", "Statut mis à jour, chéquier créé",
                        "chequierId", ch.getId(),
                        "numero", ch.getNumero(),
                        "nbFeuillets", ch.getNbFeuillets()
                ));
            }

            // Historique de changement de statut (non validée)
            historiqueRepository.save(new Historique(current, "Changement de statut de la demande: " + statutStr));

            return ResponseEntity.ok(Map.of("message","Statut mis à jour"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error","Statut invalide"));
        }
    }

    private String genererNumeroUnique() {
        String numero;
        do {
            String date = java.time.LocalDate.now().toString().replace("-", "");
            String rnd = String.valueOf((int)(Math.random()*900000)+100000);
            numero = "CHQ-" + date + "-" + rnd;
        } while (chequierRepository.existsByNumero(numero));
        return numero;
    }

    private boolean isOwnerOrStaff(User current, Demande d) {
        boolean owner = d.getClient() != null && d.getClient().getId().equals(current.getId());
        boolean staff = current.getRole() != null &&
                (current.getRole().name().equals("ADMIN") || current.getRole().name().equals("AGENT_BANCAIRE"));
        return owner || staff;
    }

    // Body JSON
    public static class DemandeBody {
        public TypeChequier typeChequier;
        public String commentaire;
    }
}
