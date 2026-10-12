package com.decoder.budgetfamilial.services;

import com.decoder.budgetfamilial.dtos.TransactionAffichageDto;
import com.decoder.budgetfamilial.dtos.TransactionCategorieEnregistrementDto;
import com.decoder.budgetfamilial.models.TitulaireCompte;
import com.decoder.budgetfamilial.models.TransactionModele;
import com.decoder.budgetfamilial.models.TypeCompte;
import com.decoder.budgetfamilial.models.TypeTransaction;
import com.decoder.budgetfamilial.repositories.CategorieRepository;
import com.decoder.budgetfamilial.repositories.TransactionRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategorieRepository categorieRepository;

    public TransactionService(TransactionRepository transactionRepository,
                              CategorieRepository categorieRepository) {
        this.transactionRepository = transactionRepository;
        this.categorieRepository = categorieRepository;
    }

    // Récupère les transactions filtrées puis les convertit en
    // TransactionAffichageDto, en ajoutant le nom et le titulaire du compte
    // (accessibles via releve.compte, ignorés en JSON sur l'entité elle-même).
    public List<TransactionAffichageDto> rechercher(LocalDate debut, LocalDate fin,
                                                    String categorie,
                                                    TitulaireCompte titulaire,
                                                    UUID compteId,
                                                    TypeCompte typeCompte) {
        return transactionRepository.rechercher(debut, fin, categorie, titulaire, compteId, typeCompte).stream()
                .map(transaction -> new TransactionAffichageDto(
                        transaction.getId(),
                        transaction.getDate(),
                        transaction.getDescription(),
                        transaction.getMontant(),
                        transaction.getTypeTransaction(),
                        transaction.getCategorie(),
                        transaction.getReleve().getCompte().getNom(),
                        transaction.getReleve().getCompte().getTitulaire()))
                .toList();
    }

    public Optional<TransactionModele> findById(UUID id) {
        return transactionRepository.findById(id);
    }

    // Refuse le changement si la transaction n'est pas une DEPENSE (seules les
    // dépenses ont une catégorie) ou si la catégorie n'existe pas en base, sinon
    // copie la nouvelle catégorie et sauvegarde.
    public TransactionModele modifierCategorie(TransactionModele transactionModele,
                                               TransactionCategorieEnregistrementDto transactionCategorieEnregistrementDto) {
        if (transactionModele.getTypeTransaction() != TypeTransaction.DEPENSE) {
            throw new IllegalArgumentException("Seules les dépenses peuvent recevoir une catégorie.");
        }
        if (!categorieRepository.existsByNom(transactionCategorieEnregistrementDto.categorie())) {
            throw new IllegalArgumentException("Catégorie inconnue.");
        }
        BeanUtils.copyProperties(transactionCategorieEnregistrementDto, transactionModele);
        return transactionRepository.save(transactionModele);
    }
}
