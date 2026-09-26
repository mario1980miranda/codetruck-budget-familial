package com.decoder.budgetfamilial.repositories;

import com.decoder.budgetfamilial.models.CompteModele;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CompteRepository extends JpaRepository<CompteModele, UUID> {
}
