package com.decoder.budgetfamilial.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "tb_comptes")
public class CompteModele implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String nom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TitulaireCompte titulaire;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TypeCompte typeCompte;

    public CompteModele() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public TitulaireCompte getTitulaire() {
        return titulaire;
    }

    public void setTitulaire(TitulaireCompte titulaire) {
        this.titulaire = titulaire;
    }

    public TypeCompte getTypeCompte() {
        return typeCompte;
    }

    public void setTypeCompte(TypeCompte typeCompte) {
        this.typeCompte = typeCompte;
    }
}
