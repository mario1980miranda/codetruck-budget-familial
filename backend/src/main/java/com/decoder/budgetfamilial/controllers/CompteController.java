package com.decoder.budgetfamilial.controllers;

import com.decoder.budgetfamilial.dtos.CompteEnregistrementDto;
import com.decoder.budgetfamilial.models.CompteModele;
import com.decoder.budgetfamilial.services.CompteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/comptes")
public class CompteController {

    private final CompteService compteService;

    public CompteController(CompteService compteService) {
        this.compteService = compteService;
    }

    @GetMapping(version = "v1")
    public ResponseEntity<List<CompteModele>> getTousLesComptes() {
        return ResponseEntity.status(HttpStatus.OK).body(compteService.findAll());
    }

    // Cherche d'abord le compte : répond 404 s'il n'existe pas,
    // sinon répond 200 avec l'entité trouvée.
    @GetMapping(value = "/{id}", version = "v1")
    public ResponseEntity<Object> getUnCompte(@PathVariable(value = "id") UUID id) {
        Optional<CompteModele> compteModeleOptional = compteService.findById(id);
        if (compteModeleOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Compte introuvable.");
        }
        return ResponseEntity.status(HttpStatus.OK).body(compteModeleOptional.get());
    }

    @PostMapping(version = "v1")
    public ResponseEntity<CompteModele> creerCompte(@RequestBody @Valid CompteEnregistrementDto compteEnregistrementDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(compteService.save(compteEnregistrementDto));
    }

    // Cherche d'abord le compte : répond 404 s'il n'existe pas,
    // sinon le met à jour et répond 200 avec l'entité mise à jour.
    @PutMapping(value = "/{id}", version = "v1")
    public ResponseEntity<Object> modifierCompte(@PathVariable(value = "id") UUID id,
                                                  @RequestBody @Valid CompteEnregistrementDto compteEnregistrementDto) {
        Optional<CompteModele> compteModeleOptional = compteService.findById(id);
        if (compteModeleOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Compte introuvable.");
        }
        return ResponseEntity.status(HttpStatus.OK).body(compteService.update(compteModeleOptional.get(), compteEnregistrementDto));
    }

    // Cherche d'abord le compte : répond 404 s'il n'existe pas,
    // sinon le supprime et répond 200.
    @DeleteMapping(value = "/{id}", version = "v1")
    public ResponseEntity<Object> supprimerCompte(@PathVariable(value = "id") UUID id) {
        Optional<CompteModele> compteModeleOptional = compteService.findById(id);
        if (compteModeleOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Compte introuvable.");
        }
        compteService.delete(compteModeleOptional.get());
        return ResponseEntity.status(HttpStatus.OK).body("Compte supprimé avec succès.");
    }
}
