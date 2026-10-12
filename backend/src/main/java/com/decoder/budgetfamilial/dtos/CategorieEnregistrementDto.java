package com.decoder.budgetfamilial.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CategorieEnregistrementDto(

        @NotBlank
        @Size(max = 20)
        @Pattern(regexp = "[A-Z_]+", message = "Le nom doit être en majuscules, sans accent ni espace (ex. ABONNEMENTS, TELECOM).")
        String nom,

        @NotBlank
        @Size(max = 300)
        String description) {
}
