package com.decoder.budgetfamilial.services;

import com.decoder.budgetfamilial.dtos.CategorieEnregistrementDto;
import com.decoder.budgetfamilial.dtos.TransactionExtraiteDto;
import com.decoder.budgetfamilial.models.CategorieModele;
import com.decoder.budgetfamilial.models.TypeTransaction;
import com.decoder.budgetfamilial.repositories.CategorieRepository;
import com.decoder.budgetfamilial.repositories.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CategorieServiceTest {

    private final CategorieRepository categorieRepository = mock(CategorieRepository.class);
    private final TransactionRepository transactionRepository = mock(TransactionRepository.class);
    private final CategorieService categorieService = new CategorieService(categorieRepository, transactionRepository);

    private CategorieModele categorie(String nom, String description) {
        CategorieModele categorie = new CategorieModele();
        categorie.setId(UUID.randomUUID());
        categorie.setNom(nom);
        categorie.setDescription(description);
        return categorie;
    }

    private TransactionExtraiteDto transaction(TypeTransaction type, String categorie) {
        return new TransactionExtraiteDto(LocalDate.of(2026, 9, 3), "Commerçant", new BigDecimal("10.00"), type, categorie);
    }

    @Test
    void findAllRetourneLesCategoriesTrieesParNom() {
        List<CategorieModele> categories = List.of(categorie("ALIMENTATION", "Épicerie"), categorie("AUTRE", "Divers"));
        when(categorieRepository.findAllByOrderByNomAsc()).thenReturn(categories);

        assertThat(categorieService.findAll()).isEqualTo(categories);
    }

    @Test
    void findByIdRetourneLaCategorieDuRepository() {
        CategorieModele categorie = categorie("TELECOM", "Téléphonie");
        when(categorieRepository.findById(categorie.getId())).thenReturn(Optional.of(categorie));

        assertThat(categorieService.findById(categorie.getId())).contains(categorie);
    }

    @Test
    void existeInterrogeLeRepositoryParNom() {
        when(categorieRepository.existsByNom("TELECOM")).thenReturn(true);

        assertThat(categorieService.existe("TELECOM")).isTrue();
        assertThat(categorieService.existe("INCONNUE")).isFalse();
    }

    @Test
    void saveCreeUneNouvelleCategorie() {
        when(categorieRepository.existsByNom("TELECOM")).thenReturn(false);
        when(categorieRepository.save(any(CategorieModele.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CategorieModele resultat = categorieService.save(new CategorieEnregistrementDto("TELECOM", "Téléphonie mobile et internet"));

        assertThat(resultat.getNom()).isEqualTo("TELECOM");
        assertThat(resultat.getDescription()).isEqualTo("Téléphonie mobile et internet");
    }

    @Test
    void saveRefuseUnNomDejaPris() {
        when(categorieRepository.existsByNom("TELECOM")).thenReturn(true);

        assertThatThrownBy(() -> categorieService.save(new CategorieEnregistrementDto("TELECOM", "Téléphonie")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Une catégorie porte déjà ce nom.");
        verify(categorieRepository, never()).save(any());
    }

    @Test
    void updateChangeLaDescriptionSansVerifierLeNom() {
        CategorieModele categorie = categorie("TELECOM", "Téléphonie");
        when(categorieRepository.save(categorie)).thenReturn(categorie);

        CategorieModele resultat = categorieService.update(categorie, new CategorieEnregistrementDto("TELECOM", "Téléphonie et internet"));

        assertThat(resultat.getDescription()).isEqualTo("Téléphonie et internet");
        verify(transactionRepository, never()).existsByCategorie(any());
    }

    @Test
    void updateRenommeUneCategorieInutilisee() {
        CategorieModele categorie = categorie("TELECON", "Téléphonie");
        when(categorieRepository.existsByNom("TELECOM")).thenReturn(false);
        when(transactionRepository.existsByCategorie("TELECON")).thenReturn(false);
        when(categorieRepository.save(categorie)).thenReturn(categorie);

        CategorieModele resultat = categorieService.update(categorie, new CategorieEnregistrementDto("TELECOM", "Téléphonie"));

        assertThat(resultat.getNom()).isEqualTo("TELECOM");
    }

    @Test
    void updateRefuseDeRenommerAutre() {
        CategorieModele categorie = categorie("AUTRE", "Divers");

        assertThatThrownBy(() -> categorieService.update(categorie, new CategorieEnregistrementDto("DIVERS", "Divers")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La catégorie AUTRE ne peut pas être renommée.");
        verify(categorieRepository, never()).save(any());
    }

    @Test
    void updateRefuseUnNouveauNomDejaPris() {
        CategorieModele categorie = categorie("TELECOM", "Téléphonie");
        when(categorieRepository.existsByNom("LOISIRS")).thenReturn(true);

        assertThatThrownBy(() -> categorieService.update(categorie, new CategorieEnregistrementDto("LOISIRS", "Téléphonie")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Une catégorie porte déjà ce nom.");
        verify(categorieRepository, never()).save(any());
    }

    @Test
    void updateRefuseDeRenommerUneCategorieUtilisee() {
        CategorieModele categorie = categorie("TELECOM", "Téléphonie");
        when(categorieRepository.existsByNom("TELEPHONIE")).thenReturn(false);
        when(transactionRepository.existsByCategorie("TELECOM")).thenReturn(true);

        assertThatThrownBy(() -> categorieService.update(categorie, new CategorieEnregistrementDto("TELEPHONIE", "Téléphonie")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cette catégorie est utilisée par des transactions : elle ne peut pas être renommée.");
        assertThat(categorie.getNom()).isEqualTo("TELECOM");
        verify(categorieRepository, never()).save(any());
    }

    @Test
    void deleteSupprimeUneCategorieInutilisee() {
        CategorieModele categorie = categorie("TELECOM", "Téléphonie");
        when(transactionRepository.existsByCategorie("TELECOM")).thenReturn(false);

        categorieService.delete(categorie);

        verify(categorieRepository).delete(categorie);
    }

    @Test
    void deleteRefuseAutre() {
        CategorieModele categorie = categorie("AUTRE", "Divers");

        assertThatThrownBy(() -> categorieService.delete(categorie))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La catégorie AUTRE ne peut pas être supprimée.");
        verify(categorieRepository, never()).delete(any());
    }

    @Test
    void deleteRefuseUneCategorieUtilisee() {
        CategorieModele categorie = categorie("TELECOM", "Téléphonie");
        when(transactionRepository.existsByCategorie("TELECOM")).thenReturn(true);

        assertThatThrownBy(() -> categorieService.delete(categorie))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cette catégorie est utilisée par des transactions : elle ne peut pas être supprimée.");
        verify(categorieRepository, never()).delete(any());
    }

    @Test
    @SuppressWarnings("unchecked")
    void initialiserCategoriesParDefautCreeLesSeptCategoriesQuandLaTableEstVide() {
        when(categorieRepository.count()).thenReturn(0L);

        categorieService.initialiserCategoriesParDefaut();

        ArgumentCaptor<List<CategorieModele>> captor = ArgumentCaptor.forClass(List.class);
        verify(categorieRepository).saveAll(captor.capture());
        assertThat(captor.getValue())
                .extracting(CategorieModele::getNom)
                .containsExactly("ALIMENTATION", "PHARMACIE", "ANIMAUX", "LOGEMENT", "TRANSPORT", "LOISIRS", "AUTRE");
    }

    @Test
    void initialiserCategoriesParDefautNeFaitRienQuandDesCategoriesExistent() {
        when(categorieRepository.count()).thenReturn(3L);

        categorieService.initialiserCategoriesParDefaut();

        verify(categorieRepository, never()).saveAll(anyList());
    }

    @Test
    void decrirePourPromptListeUneLigneParCategorie() {
        when(categorieRepository.findAllByOrderByNomAsc()).thenReturn(List.of(
                categorie("AUTRE", "Divers"),
                categorie("TELECOM", "Téléphonie mobile")));

        assertThat(categorieService.decrirePourPrompt())
                .isEqualTo("- AUTRE : Divers\n- TELECOM : Téléphonie mobile");
    }

    @Test
    void ramenerCategoriesInconnuesAAutreCorrigeSeulementLesDepensesInconnues() {
        when(categorieRepository.findAll()).thenReturn(List.of(categorie("TELECOM", "Téléphonie"), categorie("AUTRE", "Divers")));

        List<TransactionExtraiteDto> resultat = categorieService.ramenerCategoriesInconnuesAAutre(List.of(
                transaction(TypeTransaction.DEPENSE, "TELECOM"),
                transaction(TypeTransaction.DEPENSE, "INVENTEE"),
                transaction(TypeTransaction.DEPENSE, null),
                transaction(TypeTransaction.TRANSFERT_INTERNE, null)));

        assertThat(resultat)
                .extracting(TransactionExtraiteDto::categorie)
                .containsExactly("TELECOM", "AUTRE", "AUTRE", null);
    }
}
