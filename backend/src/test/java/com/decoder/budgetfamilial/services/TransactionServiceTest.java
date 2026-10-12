package com.decoder.budgetfamilial.services;

import com.decoder.budgetfamilial.dtos.TransactionAffichageDto;
import com.decoder.budgetfamilial.dtos.TransactionCategorieEnregistrementDto;
import com.decoder.budgetfamilial.models.CompteModele;
import com.decoder.budgetfamilial.models.ReleveModele;
import com.decoder.budgetfamilial.models.TitulaireCompte;
import com.decoder.budgetfamilial.models.TransactionModele;
import com.decoder.budgetfamilial.models.TypeCompte;
import com.decoder.budgetfamilial.models.TypeTransaction;
import com.decoder.budgetfamilial.repositories.CategorieRepository;
import com.decoder.budgetfamilial.repositories.TransactionRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TransactionServiceTest {

    private final TransactionRepository transactionRepository = mock(TransactionRepository.class);
    private final CategorieRepository categorieRepository = mock(CategorieRepository.class);
    private final TransactionService transactionService = new TransactionService(transactionRepository, categorieRepository);

    @Test
    void rechercherConvertitLesTransactionsAvecLeNomEtLeTitulaireDuCompte() {
        LocalDate debut = LocalDate.of(2026, 1, 1);
        LocalDate fin = LocalDate.of(2026, 1, 31);
        UUID id = UUID.randomUUID();

        CompteModele compte = new CompteModele();
        compte.setNom("Visa Mario");
        compte.setTitulaire(TitulaireCompte.MARIO);
        compte.setTypeCompte(TypeCompte.CARTE_CREDIT);

        ReleveModele releve = new ReleveModele();
        releve.setCompte(compte);

        TransactionModele transaction = new TransactionModele();
        transaction.setId(id);
        transaction.setReleve(releve);
        transaction.setDate(LocalDate.of(2026, 1, 15));
        transaction.setDescription("Épicerie");
        transaction.setMontant(new BigDecimal("85.20"));
        transaction.setTypeTransaction(TypeTransaction.DEPENSE);
        transaction.setCategorie("ALIMENTATION");

        when(transactionRepository.rechercher(debut, fin, null, null, null, TypeCompte.CARTE_CREDIT))
                .thenReturn(List.of(transaction));

        List<TransactionAffichageDto> resultat =
                transactionService.rechercher(debut, fin, null, null, null, TypeCompte.CARTE_CREDIT);

        assertThat(resultat).containsExactly(new TransactionAffichageDto(
                id,
                LocalDate.of(2026, 1, 15),
                "Épicerie",
                new BigDecimal("85.20"),
                TypeTransaction.DEPENSE,
                "ALIMENTATION",
                "Visa Mario",
                TitulaireCompte.MARIO));
    }

    @Test
    void findByIdRetourneLaTransactionDuRepository() {
        UUID id = UUID.randomUUID();
        TransactionModele transaction = new TransactionModele();
        transaction.setId(id);

        when(transactionRepository.findById(id)).thenReturn(Optional.of(transaction));

        assertThat(transactionService.findById(id)).contains(transaction);
    }

    @Test
    void modifierCategorieChangeLaCategorieDUneDepenseEtSauvegarde() {
        TransactionModele transaction = new TransactionModele();
        transaction.setTypeTransaction(TypeTransaction.DEPENSE);
        transaction.setCategorie("AUTRE");

        when(categorieRepository.existsByNom("LOISIRS")).thenReturn(true);
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        TransactionModele resultat = transactionService.modifierCategorie(
                transaction, new TransactionCategorieEnregistrementDto("LOISIRS"));

        assertThat(resultat.getCategorie()).isEqualTo("LOISIRS");
        verify(transactionRepository).save(transaction);
    }

    @Test
    void modifierCategorieRefuseUneTransactionQuiNEstPasUneDepense() {
        TransactionModele transaction = new TransactionModele();
        transaction.setTypeTransaction(TypeTransaction.TRANSFERT_INTERNE);

        assertThatThrownBy(() -> transactionService.modifierCategorie(
                transaction, new TransactionCategorieEnregistrementDto("LOISIRS")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Seules les dépenses peuvent recevoir une catégorie.");

        assertThat(transaction.getCategorie()).isNull();
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void modifierCategorieRefuseUneCategorieInconnue() {
        TransactionModele transaction = new TransactionModele();
        transaction.setTypeTransaction(TypeTransaction.DEPENSE);
        transaction.setCategorie("AUTRE");

        when(categorieRepository.existsByNom("INEXISTANTE")).thenReturn(false);

        assertThatThrownBy(() -> transactionService.modifierCategorie(
                transaction, new TransactionCategorieEnregistrementDto("INEXISTANTE")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Catégorie inconnue.");

        assertThat(transaction.getCategorie()).isEqualTo("AUTRE");
        verify(transactionRepository, never()).save(any());
    }
}
