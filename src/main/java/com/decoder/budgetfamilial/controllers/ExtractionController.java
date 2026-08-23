package com.decoder.budgetfamilial.controllers;

import com.decoder.budgetfamilial.services.PdfExtractionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/extraction")
public class ExtractionController {

    private final PdfExtractionService pdfExtractionService;

    public ExtractionController(PdfExtractionService pdfExtractionService) {
        this.pdfExtractionService = pdfExtractionService;
    }

    // Extrait le texte brut du PDF envoyé : répond 400 si le fichier n'est pas
    // un PDF lisible, sinon 200 avec le texte extrait (rien n'est encore
    // persisté ni envoyé à l'IA à cette étape - c'est juste pour vérifier
    // l'extraction elle-même).
    @PostMapping(value = "/texte", version = "v1", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> extraireTexte(@RequestParam("fichier") MultipartFile fichier) {
        try {
            String texte = pdfExtractionService.extraireTexte(fichier);
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("texte", texte));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Impossible de lire le fichier PDF.");
        }
    }
}
