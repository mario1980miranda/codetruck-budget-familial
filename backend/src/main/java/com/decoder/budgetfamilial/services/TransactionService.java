package com.decoder.budgetfamilial.services;

import com.decoder.budgetfamilial.dtos.TransactionAffichageDto;
import com.decoder.budgetfamilial.models.CategorieDepense;
import com.decoder.budgetfamilial.models.TitulaireCompte;
import com.decoder.budgetfamilial.repositories.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    // Récupère les transactions filtrées puis les convertit en
    // TransactionAffichageDto, en ajoutant le nom et le titulaire du compte
    // (accessibles via releve.compte, ignorés en JSON sur l'entité elle-même).
    public List<TransactionAffichageDto> rechercher(LocalDate debut, LocalDate fin,
                                                    CategorieDepense categorie,
                                                    TitulaireCompte titulaire,
                                                    UUID compteId) {
        return transactionRepository.rechercher(debut, fin, categorie, titulaire, compteId).stream()
                .map(transaction -> new TransactionAffichageDto(
                        transaction.getDate(),
                        transaction.getDescription(),
                        transaction.getMontant(),
                        transaction.getTypeTransaction(),
                        transaction.getCategorie(),
                        transaction.getReleve().getCompte().getNom(),
                        transaction.getReleve().getCompte().getTitulaire()))
                .toList();
    }
}