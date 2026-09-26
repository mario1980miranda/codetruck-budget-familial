package com.decoder.budgetfamilial.controllers;

import com.decoder.budgetfamilial.models.ReleveModele;
import com.decoder.budgetfamilial.services.ReleveService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/comptes/{compteId}/releves")
public class ReleveController {

    private final ReleveService releveService;

    public ReleveController(ReleveService releveService) {
        this.releveService = releveService;
    }

    // Répond 404 si le compte n'existe pas, 400 si le PDF n'est pas lisible,
    // sinon 201 avec le relevé et ses transactions persistés.
    @PostMapping(version = "v1", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> importerReleve(@PathVariable(value = "compteId") UUID compteId,
                                                 @RequestParam("fichier") MultipartFile fichier) {
        try {
            Optional<ReleveModele> releveOptional = releveService.persisterReleve(compteId, fichier);
            if (releveOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Compte introuvable.");
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(releveOptional.get());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Impossible de lire le fichier PDF.");
        }
    }
}