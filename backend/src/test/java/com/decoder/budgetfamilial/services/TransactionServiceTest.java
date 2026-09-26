package com.decoder.budgetfamilial.services;

import com.decoder.budgetfamilial.dtos.TransactionAffichageDto;
import com.decoder.budgetfamilial.models.CategorieDepense;
import com.decoder.budgetfamilial.models.CompteModele;
import com.decoder.budgetfamilial.models.ReleveModele;
import com.decoder.budgetfamilial.models.TitulaireCompte;
import com.decoder.budgetfamilial.models.TransactionModele;
import com.decoder.budgetfamilial.models.TypeCompte;
import com.decoder.budgetfamilial.models.TypeTransaction;
import com.decoder.budgetfamilial.repositories.TransactionRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TransactionServiceTest {

    private final TransactionRepository transactionRepository = mock(TransactionRepository.class);
    private final TransactionService transactionService = new TransactionService(transactionRepository);

    @Test
    void rechercherConvertitLesTransactionsAvecLeNomEtLeTitulaireDuCompte() {
        LocalDate debut = LocalDate.of(2026, 1, 1);
        LocalDate fin = LocalDate.of(2026, 1, 31);

        CompteModele compte = new CompteModele();
        compte.setNom("Visa Mario");
        compte.setTitulaire(TitulaireCompte.MARIO);
        compte.setTypeCompte(TypeCompte.CARTE_CREDIT);

        ReleveModele releve = new ReleveModele();
        releve.setCompte(compte);

        TransactionModele transaction = new TransactionModele();
        transaction.setReleve(releve);
        transaction.setDate(LocalDate.of(2026, 1, 15));
        transaction.setDescription("Épicerie");
        transaction.setMontant(new BigDecimal("85.20"));
        transaction.setTypeTransaction(TypeTransaction.DEPENSE);
        transaction.setCategorie(CategorieDepense.ALIMENTATION);

        when(transactionRepository.rechercher(debut, fin, null, null, null, TypeCompte.CARTE_CREDIT))
                .thenReturn(List.of(transaction));

        List<TransactionAffichageDto> resultat =
                transactionService.rechercher(debut, fin, null, null, null, TypeCompte.CARTE_CREDIT);

        assertThat(resultat).containsExactly(new TransactionAffichageDto(
                LocalDate.of(2026, 1, 15),
                "Épicerie",
                new BigDecimal("85.20"),
                TypeTransaction.DEPENSE,
                CategorieDepense.ALIMENTATION,
                "Visa Mario",
                TitulaireCompte.MARIO));
    }
}
