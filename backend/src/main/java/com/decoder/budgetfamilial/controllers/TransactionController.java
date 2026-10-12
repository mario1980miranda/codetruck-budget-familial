package com.decoder.budgetfamilial.controllers;

import com.decoder.budgetfamilial.dtos.TransactionAffichageDto;
import com.decoder.budgetfamilial.dtos.TransactionCategorieEnregistrementDto;
import com.decoder.budgetfamilial.models.TitulaireCompte;
import com.decoder.budgetfamilial.models.TransactionModele;
import com.decoder.budgetfamilial.models.TypeCompte;
import com.decoder.budgetfamilial.services.TransactionService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping(version = "v1")
    public ResponseEntity<List<TransactionAffichageDto>> listerTransactions(
            @RequestParam("debut") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam("fin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin,
            @RequestParam(value = "categorie", required = false) String categorie,
            @RequestParam(value = "titulaire", required = false) TitulaireCompte titulaire,
            @RequestParam(value = "compteId", required = false) UUID compteId,
            @RequestParam(value = "typeCompte", required = false) TypeCompte typeCompte) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(transactionService.rechercher(debut, fin, categorie, titulaire, compteId, typeCompte));
    }

    // Cherche d'abord la transaction : répond 404 si elle n'existe pas,
    // 400 si ce n'est pas une dépense, sinon 200 avec l'entité mise à jour.
    @PatchMapping(value = "/{id}/categorie", version = "v1")
    public ResponseEntity<Object> modifierCategorie(@PathVariable(value = "id") UUID id,
                                                    @RequestBody @Valid TransactionCategorieEnregistrementDto transactionCategorieEnregistrementDto) {
        Optional<TransactionModele> transactionModeleOptional = transactionService.findById(id);
        if (transactionModeleOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Transaction introuvable.");
        }
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(transactionService.modifierCategorie(transactionModeleOptional.get(), transactionCategorieEnregistrementDto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
