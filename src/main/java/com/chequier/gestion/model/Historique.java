package com.chequier.gestion.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historiques")
public class Historique {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Utilisateur qui a effectué l’action
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User utilisateur;

    private String action; // ex: "Création demande", "Chequier délivré"

    private LocalDateTime dateAction = LocalDateTime.now();

    public Historique() {}

    public Historique(User utilisateur, String action) {
        this.utilisateur = utilisateur;
        this.action = action;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public User getUtilisateur() { return utilisateur; }
    public void setUtilisateur(User utilisateur) { this.utilisateur = utilisateur; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public LocalDateTime getDateAction() { return dateAction; }
    public void setDateAction(LocalDateTime dateAction) { this.dateAction = dateAction; }
}
