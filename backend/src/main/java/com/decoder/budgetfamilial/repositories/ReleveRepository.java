package com.decoder.budgetfamilial.repositories;

import com.decoder.budgetfamilial.models.ReleveModele;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReleveRepository extends JpaRepository<ReleveModele, UUID> {
}