package com.decoder.budgetfamilial.dtos;

import com.decoder.budgetfamilial.models.CategorieDepense;
import com.decoder.budgetfamilial.models.TypeTransaction;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionExtraiteDto(
        LocalDate date,
        String description,
        BigDecimal montant,
        TypeTransaction typeTransaction,
        CategorieDepense categorie) {
}
