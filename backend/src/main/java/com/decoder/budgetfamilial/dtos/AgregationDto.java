package com.decoder.budgetfamilial.dtos;

import com.decoder.budgetfamilial.models.CategorieDepense;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public record AgregationDto(
        LocalDate periodeDebut,
        LocalDate periodeFin,
        BigDecimal totalRevenus,
        BigDecimal totalDepenses,
        BigDecimal totalEpargne,
        Map<CategorieDepense, BigDecimal> depensesParCategorie) {
}