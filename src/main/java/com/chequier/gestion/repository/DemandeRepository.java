package com.chequier.gestion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chequier.gestion.model.Demande;
import com.chequier.gestion.model.User;

public interface DemandeRepository extends JpaRepository<Demande, Long> {
    long countByStatut(com.chequier.gestion.model.StatutDemande statut);

    List<Demande> findByClient(User client);
}
