package com.decoder.budgetfamilial.controllers;

import com.decoder.budgetfamilial.dtos.TransactionExtraiteDto;
import com.decoder.budgetfamilial.services.CategorisationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/categorisation")
public class CategorisationController {

    private final CategorisationService categorisationService;

    public CategorisationController(CategorisationService categorisationService) {
        this.categorisationService = categorisationService;
    }

    // Extrait et catégorise les transactions du PDF envoyé : répond 400 si le
    // fichier n'est pas un PDF lisible, sinon 200 avec la liste structurée par
    // Claude Haiku. Rien n'est encore persisté à cette étape.
    @PostMapping(value = "/pdf", version = "v1", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> categoriserPdf(@RequestParam("fichier") MultipartFile fichier) {
        try {
            List<TransactionExtraiteDto> transactions = categorisationService.categoriserReleve(fichier);
            return ResponseEntity.status(HttpStatus.OK).body(transactions);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Impossible de lire le fichier PDF.");
        }
    }
}