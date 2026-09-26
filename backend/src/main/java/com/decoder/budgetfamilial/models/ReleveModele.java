package com.decoder.budgetfamilial.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tb_releves")
public class ReleveModele implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "compte_id", nullable = false)
    private CompteModele compte;

    @Column(nullable = false)
    private LocalDate dateImport;

    @Column(nullable = false)
    private LocalDate periodeDebut;

    @Column(nullable = false)
    private LocalDate periodeFin;

    @OneToMany(mappedBy = "releve", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("date ASC")
    private List<TransactionModele> transactions = new ArrayList<>();

    public ReleveModele() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public CompteModele getCompte() {
        return compte;
    }

    public void setCompte(CompteModele compte) {
        this.compte = compte;
    }

    public LocalDate getDateImport() {
        return dateImport;
    }

    public void setDateImport(LocalDate dateImport) {
        this.dateImport = dateImport;
    }

    public LocalDate getPeriodeDebut() {
        return periodeDebut;
    }

    public void setPeriodeDebut(LocalDate periodeDebut) {
        this.periodeDebut = periodeDebut;
    }

    public LocalDate getPeriodeFin() {
        return periodeFin;
    }

    public void setPeriodeFin(LocalDate periodeFin) {
        this.periodeFin = periodeFin;
    }

    public List<TransactionModele> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionModele> transactions) {
        this.transactions = transactions;
    }
}