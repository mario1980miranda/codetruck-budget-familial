package com.decoder.budgetfamilial.services;

import com.decoder.budgetfamilial.dtos.CompteEnregistrementDto;
import com.decoder.budgetfamilial.models.CompteModele;
import com.decoder.budgetfamilial.repositories.CompteRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CompteService {

    private final CompteRepository compteRepository;

    public CompteService(CompteRepository compteRepository) {
        this.compteRepository = compteRepository;
    }

    public List<CompteModele> findAll() {
        return compteRepository.findAll();
    }

    public Optional<CompteModele> findById(UUID id) {
        return compteRepository.findById(id);
    }

    public CompteModele save(CompteEnregistrementDto compteEnregistrementDto) {
        var compteModele = new CompteModele();
        BeanUtils.copyProperties(compteEnregistrementDto, compteModele);
        return compteRepository.save(compteModele);
    }

    public CompteModele update(CompteModele compteModele, CompteEnregistrementDto compteEnregistrementDto) {
        BeanUtils.copyProperties(compteEnregistrementDto, compteModele);
        return compteRepository.save(compteModele);
    }

    public void delete(CompteModele compteModele) {
        compteRepository.delete(compteModele);
    }
}
