package com.chequier.gestion.back.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chequier.gestion.back.model.Demande;
import com.chequier.gestion.back.model.User;

public interface DemandeRepository extends JpaRepository<Demande, Long> {
    long countByStatut(com.chequier.gestion.back.model.StatutDemande statut);

    List<Demande> findByClient(User client);
}
