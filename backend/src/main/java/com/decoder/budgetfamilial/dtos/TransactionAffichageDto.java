package com.decoder.budgetfamilial.dtos;

import com.decoder.budgetfamilial.models.TitulaireCompte;
import com.decoder.budgetfamilial.models.TypeTransaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransactionAffichageDto(
        UUID id,
        LocalDate date,
        String description,
        BigDecimal montant,
        TypeTransaction typeTransaction,
        String categorie,
        String compteNom,
        TitulaireCompte titulaire) {
}