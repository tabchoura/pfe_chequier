package com.chequier.gestion.repository;

import com.chequier.gestion.model.Historique;
import com.chequier.gestion.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoriqueRepository extends JpaRepository<Historique, Long> {
    List<Historique> findByUtilisateur(User utilisateur);
}
