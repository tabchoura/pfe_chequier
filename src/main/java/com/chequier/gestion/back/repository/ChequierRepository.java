package com.chequier.gestion.back.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chequier.gestion.back.model.Chequier;
import com.chequier.gestion.back.model.User;

public interface ChequierRepository extends JpaRepository<Chequier, Long> {
    List<Chequier> findByClient(User client);
    long countByStatut(com.chequier.gestion.back.model.StatutChequier statut);

    boolean existsByNumero(String numero);
    
}
