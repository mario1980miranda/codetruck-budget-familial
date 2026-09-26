package com.decoder.budgetfamilial.services;

import com.decoder.budgetfamilial.dtos.TransactionExtraiteDto;
import com.decoder.budgetfamilial.models.CompteModele;
import com.decoder.budgetfamilial.models.ReleveModele;
import com.decoder.budgetfamilial.models.TransactionModele;
import com.decoder.budgetfamilial.repositories.CompteRepository;
import com.decoder.budgetfamilial.repositories.ReleveRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ReleveService {

    private final CompteRepository compteRepository;
    private final ReleveRepository releveRepository;
    private final CategorisationService categorisationService;

    public ReleveService(CompteRepository compteRepository,
                         ReleveRepository releveRepository,
                         CategorisationService categorisationService) {
        this.compteRepository = compteRepository;
        this.releveRepository = releveRepository;
        this.categorisationService = categorisationService;
    }

    // Vérifie d'abord que le compte existe. Sinon, extrait/catégorise le PDF,
    // parcourt les transactions pour déterminer la période couverte (date min et
    // max), puis sauvegarde le relevé avec ses transactions en cascade.
    public Optional<ReleveModele> persisterReleve(UUID compteId, MultipartFile fichier) throws IOException {
        Optional<CompteModele> compteOptional = compteRepository.findById(compteId);
        if (compteOptional.isEmpty()) {
            return Optional.empty();
        }

        List<TransactionExtraiteDto> transactionsExtraites = categorisationService.categoriserReleve(fichier);

        var releve = new ReleveModele();
        releve.setCompte(compteOptional.get());
        releve.setDateImport(LocalDate.now());

        LocalDate periodeDebut = null;
        LocalDate periodeFin = null;
        for (TransactionExtraiteDto dto : transactionsExtraites) {
            if (periodeDebut == null || dto.date().isBefore(periodeDebut)) {
                periodeDebut = dto.date();
            }
            if (periodeFin == null || dto.date().isAfter(periodeFin)) {
                periodeFin = dto.date();
            }

            var transaction = new TransactionModele();
            BeanUtils.copyProperties(dto, transaction);
            transaction.setReleve(releve);
            releve.getTransactions().add(transaction);
        }
        releve.setPeriodeDebut(periodeDebut);
        releve.setPeriodeFin(periodeFin);

        return Optional.of(releveRepository.save(releve));
    }
}