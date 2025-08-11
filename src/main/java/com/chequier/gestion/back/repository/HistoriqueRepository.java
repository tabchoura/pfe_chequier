package com.chequier.gestion.back.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chequier.gestion.back.model.Historique;
import com.chequier.gestion.back.model.User;

public interface HistoriqueRepository extends JpaRepository<Historique, Long> {
    List<Historique> findByUtilisateur(User utilisateur);
}
