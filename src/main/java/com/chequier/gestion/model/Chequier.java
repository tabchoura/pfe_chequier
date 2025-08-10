package com.chequier.gestion.model;

import java.time.LocalDate;

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
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "chequiers", uniqueConstraints = @UniqueConstraint(name = "uk_numero_chequier", columnNames = "numero"))
public class Chequier {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String numero; // ex: CHQ-20250810-123456

    @ManyToOne @JoinColumn(name = "client_id", nullable = false)
    private User client;

    @ManyToOne @JoinColumn(name = "demande_id", nullable = false)
    private Demande demande;

    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private StatutChequier statut = StatutChequier.ACTIF;

    @Column(nullable = false)
    private int nbFeuillets; // 25 ou 50

    @Column(nullable = false)
    private LocalDate dateEmission = LocalDate.now();

    public Chequier() {}

    public Chequier(String numero, User client, Demande demande, int nbFeuillets) {
        this.numero = numero;
        this.client = client;
        this.demande = demande;
        this.nbFeuillets = nbFeuillets;
    }

    public Long getId() { return id; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public User getClient() { return client; }
    public void setClient(User client) { this.client = client; }
    public Demande getDemande() { return demande; }
    public void setDemande(Demande demande) { this.demande = demande; }
    public StatutChequier getStatut() { return statut; }
    public void setStatut(StatutChequier statut) { this.statut = statut; }
    public int getNbFeuillets() { return nbFeuillets; }
    public void setNbFeuillets(int nbFeuillets) { this.nbFeuillets = nbFeuillets; }
    public LocalDate getDateEmission() { return dateEmission; }
    public void setDateEmission(LocalDate dateEmission) { this.dateEmission = dateEmission; }
}
