package com.decoder.budgetfamilial.dtos;

import com.decoder.budgetfamilial.models.TitulaireCompte;
import com.decoder.budgetfamilial.models.TypeCompte;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CompteEnregistrementDto(

        @NotBlank
        @Size(max = 100)
        String nom,

        @NotNull
        TitulaireCompte titulaire,

        @NotNull
        TypeCompte typeCompte) {
}
