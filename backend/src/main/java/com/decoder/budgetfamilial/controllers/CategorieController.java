package com.decoder.budgetfamilial.controllers;

import com.decoder.budgetfamilial.dtos.CategorieEnregistrementDto;
import com.decoder.budgetfamilial.models.CategorieModele;
import com.decoder.budgetfamilial.services.CategorieService;
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
@RequestMapping("/categories")
public class CategorieController {

    private final CategorieService categorieService;

    public CategorieController(CategorieService categorieService) {
        this.categorieService = categorieService;
    }

    @GetMapping(version = "v1")
    public ResponseEntity<List<CategorieModele>> getToutesLesCategories() {
        return ResponseEntity.status(HttpStatus.OK).body(categorieService.findAll());
    }

    // Répond 409 si le nom est déjà pris, sinon 201 avec la catégorie créée.
    @PostMapping(version = "v1")
    public ResponseEntity<Object> creerCategorie(@RequestBody @Valid CategorieEnregistrementDto categorieEnregistrementDto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(categorieService.save(categorieEnregistrementDto));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    // Cherche d'abord la catégorie : répond 404 si elle n'existe pas, 400 si on
    // tente de renommer AUTRE, 409 si le nouveau nom est pris ou si la catégorie
    // renommée est utilisée, sinon 200 avec l'entité mise à jour.
    @PutMapping(value = "/{id}", version = "v1")
    public ResponseEntity<Object> modifierCategorie(@PathVariable(value = "id") UUID id,
                                                    @RequestBody @Valid CategorieEnregistrementDto categorieEnregistrementDto) {
        Optional<CategorieModele> categorieModeleOptional = categorieService.findById(id);
        if (categorieModeleOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Catégorie introuvable.");
        }
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(categorieService.update(categorieModeleOptional.get(), categorieEnregistrementDto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    // Cherche d'abord la catégorie : répond 404 si elle n'existe pas, 400 pour
    // AUTRE, 409 si des transactions l'utilisent, sinon la supprime et répond 200.
    @DeleteMapping(value = "/{id}", version = "v1")
    public ResponseEntity<Object> supprimerCategorie(@PathVariable(value = "id") UUID id) {
        Optional<CategorieModele> categorieModeleOptional = categorieService.findById(id);
        if (categorieModeleOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Catégorie introuvable.");
        }
        try {
            categorieService.delete(categorieModeleOptional.get());
            return ResponseEntity.status(HttpStatus.OK).body("Catégorie supprimée avec succès.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
}
