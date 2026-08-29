package com.decoder.budgetfamilial.controllers;

import com.decoder.budgetfamilial.dtos.TransactionAffichageDto;
import com.decoder.budgetfamilial.models.CategorieDepense;
import com.decoder.budgetfamilial.models.TitulaireCompte;
import com.decoder.budgetfamilial.services.TransactionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
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
            @RequestParam(value = "categorie", required = false) CategorieDepense categorie,
            @RequestParam(value = "titulaire", required = false) TitulaireCompte titulaire,
            @RequestParam(value = "compteId", required = false) UUID compteId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(transactionService.rechercher(debut, fin, categorie, titulaire, compteId));
    }
}