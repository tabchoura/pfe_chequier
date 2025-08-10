package com.chequier.gestion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chequier.gestion.model.Chequier;
import com.chequier.gestion.model.User;

public interface ChequierRepository extends JpaRepository<Chequier, Long> {
    List<Chequier> findByClient(User client);
    long countByStatut(com.chequier.gestion.model.StatutChequier statut);

    boolean existsByNumero(String numero);
    
}
