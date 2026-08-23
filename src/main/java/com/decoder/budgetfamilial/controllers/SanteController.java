package com.decoder.budgetfamilial.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/sante")
public class SanteController {

    @GetMapping(version = "v1")
    public ResponseEntity<Map<String, String>> verifierSante() {
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("statut", "OK"));
    }
}
