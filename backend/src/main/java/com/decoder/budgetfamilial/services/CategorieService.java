package com.decoder.budgetfamilial.services;

import com.decoder.budgetfamilial.dtos.CategorieEnregistrementDto;
import com.decoder.budgetfamilial.dtos.TransactionExtraiteDto;
import com.decoder.budgetfamilial.models.CategorieModele;
import com.decoder.budgetfamilial.models.TypeTransaction;
import com.decoder.budgetfamilial.repositories.CategorieRepository;
import com.decoder.budgetfamilial.repositories.TransactionRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CategorieService {

    public static final String AUTRE = "AUTRE";

    private static final Map<String, String> CATEGORIES_PAR_DEFAUT = new LinkedHashMap<>();

    static {
        CATEGORIES_PAR_DEFAUT.put("ALIMENTATION", "Épiceries, supermarchés, boulangeries, restaurants, cafés et livraison de repas.");
        CATEGORIES_PAR_DEFAUT.put("PHARMACIE", "Pharmacies (Jean Coutu, Pharmaprix, Familiprix, Uniprix), médicaments et produits de santé.");
        CATEGORIES_PAR_DEFAUT.put("ANIMAUX", "Animaleries, vétérinaires, nourriture et soins pour animaux.");
        CATEGORIES_PAR_DEFAUT.put("LOGEMENT", "Loyer, hypothèque, électricité, chauffage, taxes municipales, quincaillerie, entretien de la maison, assurance dont le nom mentionne explicitement « habitation » ou « condo ».");
        CATEGORIES_PAR_DEFAUT.put("TRANSPORT", "Essence, transport en commun, stationnement, péages, taxi, Uber, entretien et immatriculation de véhicule.");
        CATEGORIES_PAR_DEFAUT.put("LOISIRS", "Divertissement : cinéma, spectacles, sorties, sports, jeux, voyages, abonnements de divertissement (Netflix, Spotify).");
        CATEGORIES_PAR_DEFAUT.put(AUTRE, "Dépense qui ne correspond clairement à aucune autre catégorie, ou commerçant inconnu.");
    }

    private final CategorieRepository categorieRepository;
    private final TransactionRepository transactionRepository;

    public CategorieService(CategorieRepository categorieRepository,
                            TransactionRepository transactionRepository) {
        this.categorieRepository = categorieRepository;
        this.transactionRepository = transactionRepository;
    }

    public List<CategorieModele> findAll() {
        return categorieRepository.findAllByOrderByNomAsc();
    }

    public Optional<CategorieModele> findById(UUID id) {
        return categorieRepository.findById(id);
    }

    public boolean existe(String nom) {
        return categorieRepository.existsByNom(nom);
    }

    // Refuse un nom déjà pris (conflit), sinon copie le DTO dans une nouvelle
    // entité et la sauvegarde.
    public CategorieModele save(CategorieEnregistrementDto categorieEnregistrementDto) {
        if (categorieRepository.existsByNom(categorieEnregistrementDto.nom())) {
            throw new IllegalStateException("Une catégorie porte déjà ce nom.");
        }
        var categorieModele = new CategorieModele();
        BeanUtils.copyProperties(categorieEnregistrementDto, categorieModele);
        return categorieRepository.save(categorieModele);
    }

    // Si le nom change : refuse de renommer AUTRE (requête invalide), refuse un
    // nom déjà pris ou une catégorie déjà utilisée par des transactions (conflit,
    // car elles référencent l'ancien nom). La description peut toujours changer.
    public CategorieModele update(CategorieModele categorieModele, CategorieEnregistrementDto categorieEnregistrementDto) {
        String ancienNom = categorieModele.getNom();
        if (!ancienNom.equals(categorieEnregistrementDto.nom())) {
            if (AUTRE.equals(ancienNom)) {
                throw new IllegalArgumentException("La catégorie AUTRE ne peut pas être renommée.");
            }
            if (categorieRepository.existsByNom(categorieEnregistrementDto.nom())) {
                throw new IllegalStateException("Une catégorie porte déjà ce nom.");
            }
            if (transactionRepository.existsByCategorie(ancienNom)) {
                throw new IllegalStateException("Cette catégorie est utilisée par des transactions : elle ne peut pas être renommée.");
            }
        }
        BeanUtils.copyProperties(categorieEnregistrementDto, categorieModele);
        return categorieRepository.save(categorieModele);
    }

    // Refuse de supprimer AUTRE (requête invalide) ou une catégorie encore
    // utilisée par des transactions (conflit), sinon la supprime.
    public void delete(CategorieModele categorieModele) {
        if (AUTRE.equals(categorieModele.getNom())) {
            throw new IllegalArgumentException("La catégorie AUTRE ne peut pas être supprimée.");
        }
        if (transactionRepository.existsByCategorie(categorieModele.getNom())) {
            throw new IllegalStateException("Cette catégorie est utilisée par des transactions : elle ne peut pas être supprimée.");
        }
        categorieRepository.delete(categorieModele);
    }

    // Si la table est vide (premier démarrage), crée les catégories par défaut,
    // qui correspondent aux anciennes valeurs fixes déjà stockées dans
    // tb_transactions. Ne fait rien sinon, pour respecter les suppressions.
    public void initialiserCategoriesParDefaut() {
        if (categorieRepository.count() > 0) {
            return;
        }
        List<CategorieModele> categories = new ArrayList<>();
        for (Map.Entry<String, String> entree : CATEGORIES_PAR_DEFAUT.entrySet()) {
            var categorieModele = new CategorieModele();
            categorieModele.setNom(entree.getKey());
            categorieModele.setDescription(entree.getValue());
            categories.add(categorieModele);
        }
        categorieRepository.saveAll(categories);
    }

    // Construit la liste « - NOM : description », une ligne par catégorie,
    // insérée dans le prompt de catégorisation envoyé à Claude.
    public String decrirePourPrompt() {
        return categorieRepository.findAllByOrderByNomAsc().stream()
                .map(categorie -> "- " + categorie.getNom() + " : " + categorie.getDescription())
                .collect(Collectors.joining("\n"));
    }

    // Parcourt les transactions retournées par Claude : une DEPENSE sans
    // catégorie ou avec une catégorie qui n'existe pas en base passe en AUTRE ;
    // les autres transactions sont gardées telles quelles.
    public List<TransactionExtraiteDto> ramenerCategoriesInconnuesAAutre(List<TransactionExtraiteDto> transactions) {
        Set<String> nomsConnus = categorieRepository.findAll().stream()
                .map(CategorieModele::getNom)
                .collect(Collectors.toSet());
        List<TransactionExtraiteDto> resultat = new ArrayList<>();
        for (TransactionExtraiteDto transaction : transactions) {
            if (transaction.typeTransaction() == TypeTransaction.DEPENSE && !nomsConnus.contains(transaction.categorie())) {
                resultat.add(new TransactionExtraiteDto(
                        transaction.date(),
                        transaction.description(),
                        transaction.montant(),
                        transaction.typeTransaction(),
                        AUTRE));
            } else {
                resultat.add(transaction);
            }
        }
        return resultat;
    }
}
