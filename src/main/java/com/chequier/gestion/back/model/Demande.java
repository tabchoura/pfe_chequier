package com.chequier.gestion.back.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "demandes")
public class Demande {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name="client_id", nullable=false)
    private User client;

    @Enumerated(EnumType.STRING) @Column(nullable=false)
    private TypeChequier typeChequier;

    @Enumerated(EnumType.STRING) @Column(nullable=false)
    private StatutDemande statut = StatutDemande.EN_ATTENTE;

    @Column(nullable=false)
    private LocalDateTime dateDemande = LocalDateTime.now();

    private String commentaire;

    public Demande() {}
    public Demande(User client, TypeChequier typeChequier, String commentaire) {
        this.client = client; this.typeChequier = typeChequier; this.commentaire = commentaire;
    }

    public Long getId() { return id; }
    public User getClient() { return client; }
    public void setClient(User client) { this.client = client; }
    public TypeChequier getTypeChequier() { return typeChequier; }
    public void setTypeChequier(TypeChequier typeChequier) { this.typeChequier = typeChequier; }
    public StatutDemande getStatut() { return statut; }
    public void setStatut(StatutDemande statut) { this.statut = statut; }
    public LocalDateTime getDateDemande() { return dateDemande; }
    public void setDateDemande(LocalDateTime dateDemande) { this.dateDemande = dateDemande; }
    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }
}
