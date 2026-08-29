package com.decoder.budgetfamilial.repositories;

import com.decoder.budgetfamilial.models.TitulaireCompte;
import com.decoder.budgetfamilial.models.TransactionModele;
import com.decoder.budgetfamilial.models.TypeTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionModele, UUID> {

    @Query("""
            SELECT t.categorie, SUM(t.montant)
            FROM TransactionModele t
            WHERE t.typeTransaction = 'DEPENSE'
              AND t.date BETWEEN :debut AND :fin
              AND (:titulaire IS NULL OR t.releve.compte.titulaire = :titulaire)
              AND (:compteId IS NULL OR t.releve.compte.id = :compteId)
            GROUP BY t.categorie
            """)
    List<Object[]> totauxParCategorie(@Param("debut") LocalDate debut,
                                      @Param("fin") LocalDate fin,
                                      @Param("titulaire") TitulaireCompte titulaire,
                                      @Param("compteId") UUID compteId);

    @Query("""
            SELECT COALESCE(SUM(t.montant), 0)
            FROM TransactionModele t
            WHERE t.typeTransaction = :typeTransaction
              AND t.date BETWEEN :debut AND :fin
              AND (:titulaire IS NULL OR t.releve.compte.titulaire = :titulaire)
              AND (:compteId IS NULL OR t.releve.compte.id = :compteId)
            """)
    BigDecimal totalParType(@Param("typeTransaction") TypeTransaction typeTransaction,
                            @Param("debut") LocalDate debut,
                            @Param("fin") LocalDate fin,
                            @Param("titulaire") TitulaireCompte titulaire,
                            @Param("compteId") UUID compteId);
}