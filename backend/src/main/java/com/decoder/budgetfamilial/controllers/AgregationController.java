package com.decoder.budgetfamilial.controllers;

import com.decoder.budgetfamilial.dtos.AgregationDto;
import com.decoder.budgetfamilial.models.TitulaireCompte;
import com.decoder.budgetfamilial.services.AgregationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/agregations")
public class AgregationController {

    private final AgregationService agregationService;

    public AgregationController(AgregationService agregationService) {
        this.agregationService = agregationService;
    }

    @GetMapping(version = "v1")
    public ResponseEntity<AgregationDto> obtenirAgregation(
            @RequestParam("debut") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam("fin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin,
            @RequestParam(value = "titulaire", required = false) TitulaireCompte titulaire,
            @RequestParam(value = "compteId", required = false) UUID compteId) {
        return ResponseEntity.status(HttpStatus.OK).body(agregationService.calculer(debut, fin, titulaire, compteId));
    }
}