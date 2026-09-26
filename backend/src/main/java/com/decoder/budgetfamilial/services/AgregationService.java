package com.decoder.budgetfamilial.services;

import com.decoder.budgetfamilial.dtos.AgregationDto;
import com.decoder.budgetfamilial.models.CategorieDepense;
import com.decoder.budgetfamilial.models.TitulaireCompte;
import com.decoder.budgetfamilial.models.TypeTransaction;
import com.decoder.budgetfamilial.repositories.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AgregationService {

    private final TransactionRepository transactionRepository;

    public AgregationService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    // Regroupe les dépenses par catégorie (filtrées par titulaire/compte si
    // fournis), puis calcule les totaux de revenus, dépenses et épargne sur la
    // période demandée avec les mêmes filtres.
    public AgregationDto calculer(LocalDate debut, LocalDate fin, TitulaireCompte titulaire, UUID compteId) {
        Map<CategorieDepense, BigDecimal> depensesParCategorie = new EnumMap<>(CategorieDepense.class);
        List<Object[]> lignes = transactionRepository.totauxParCategorie(debut, fin, titulaire, compteId);
        for (Object[] ligne : lignes) {
            depensesParCategorie.put((CategorieDepense) ligne[0], (BigDecimal) ligne[1]);
        }

        BigDecimal totalDepenses = depensesParCategorie.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalRevenus = transactionRepository.totalParType(TypeTransaction.REVENU, debut, fin, titulaire, compteId);
        BigDecimal totalEpargne = transactionRepository.totalParType(TypeTransaction.EPARGNE, debut, fin, titulaire, compteId);

        return new AgregationDto(debut, fin, totalRevenus, totalDepenses, totalEpargne, depensesParCategorie);
    }
}