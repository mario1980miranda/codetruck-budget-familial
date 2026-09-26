package com.decoder.budgetfamilial.services;

import com.decoder.budgetfamilial.dtos.AgregationDto;
import com.decoder.budgetfamilial.models.CategorieDepense;
import com.decoder.budgetfamilial.models.TitulaireCompte;
import com.decoder.budgetfamilial.models.TypeCompte;
import com.decoder.budgetfamilial.models.TypeTransaction;
import com.decoder.budgetfamilial.repositories.TransactionRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AgregationServiceTest {

    private final TransactionRepository transactionRepository = mock(TransactionRepository.class);
    private final AgregationService agregationService = new AgregationService(transactionRepository);

    private final LocalDate debut = LocalDate.of(2026, 1, 1);
    private final LocalDate fin = LocalDate.of(2026, 1, 31);

    @Test
    void calculerAdditionneLesDepensesParCategorieEtRetourneLesTotaux() {
        UUID compteId = UUID.randomUUID();
        List<Object[]> lignes = List.of(
                new Object[]{CategorieDepense.ALIMENTATION, new BigDecimal("120.50")},
                new Object[]{CategorieDepense.TRANSPORT, new BigDecimal("30.00")});
        when(transactionRepository.totauxParCategorie(debut, fin, TitulaireCompte.MARIO, compteId, TypeCompte.CARTE_CREDIT))
                .thenReturn(lignes);
        when(transactionRepository.totalParType(TypeTransaction.REVENU, debut, fin, TitulaireCompte.MARIO, compteId, TypeCompte.CARTE_CREDIT))
                .thenReturn(new BigDecimal("2000.00"));
        when(transactionRepository.totalParType(TypeTransaction.EPARGNE, debut, fin, TitulaireCompte.MARIO, compteId, TypeCompte.CARTE_CREDIT))
                .thenReturn(new BigDecimal("150.00"));

        AgregationDto resultat = agregationService.calculer(debut, fin, TitulaireCompte.MARIO, compteId, TypeCompte.CARTE_CREDIT);

        assertThat(resultat.periodeDebut()).isEqualTo(debut);
        assertThat(resultat.periodeFin()).isEqualTo(fin);
        assertThat(resultat.totalDepenses()).isEqualByComparingTo("150.50");
        assertThat(resultat.totalRevenus()).isEqualByComparingTo("2000.00");
        assertThat(resultat.totalEpargne()).isEqualByComparingTo("150.00");
        assertThat(resultat.depensesParCategorie())
                .containsEntry(CategorieDepense.ALIMENTATION, new BigDecimal("120.50"))
                .containsEntry(CategorieDepense.TRANSPORT, new BigDecimal("30.00"))
                .hasSize(2);
    }

    @Test
    void calculerSansDepensesRetourneUnTotalDeDepensesAZero() {
        when(transactionRepository.totauxParCategorie(debut, fin, null, null, null)).thenReturn(List.of());
        when(transactionRepository.totalParType(TypeTransaction.REVENU, debut, fin, null, null, null))
                .thenReturn(BigDecimal.ZERO);
        when(transactionRepository.totalParType(TypeTransaction.EPARGNE, debut, fin, null, null, null))
                .thenReturn(BigDecimal.ZERO);

        AgregationDto resultat = agregationService.calculer(debut, fin, null, null, null);

        assertThat(resultat.totalDepenses()).isEqualByComparingTo("0");
        assertThat(resultat.depensesParCategorie()).isEmpty();
    }
}
