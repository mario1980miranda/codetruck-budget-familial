package com.decoder.budgetfamilial.repositories;

import com.decoder.budgetfamilial.models.CategorieModele;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CategorieRepository extends JpaRepository<CategorieModele, UUID> {

    boolean existsByNom(String nom);

    List<CategorieModele> findAllByOrderByNomAsc();
}
