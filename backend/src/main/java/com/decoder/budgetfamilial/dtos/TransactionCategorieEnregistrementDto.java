package com.decoder.budgetfamilial.dtos;

import jakarta.validation.constraints.NotBlank;

public record TransactionCategorieEnregistrementDto(

        @NotBlank
        String categorie) {
}
