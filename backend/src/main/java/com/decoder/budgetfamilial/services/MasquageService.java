package com.decoder.budgetfamilial.services;

import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class MasquageService {

    private static final Pattern NUMERO_CARTE = Pattern.compile("\\d{4} \\d{2}\\*\\* \\*{4} \\d{4}");
    private static final Pattern NUMERO_CONTRAT = Pattern.compile("C\\d{11}");
    private static final Pattern FOLIO = Pattern.compile("(?i)folio\\s+\\d+");
    private static final Pattern CODE_POSTAL = Pattern.compile("[A-Z]\\d[A-Z]\\s?\\d[A-Z]\\d");
    private static final Pattern ADRESSE_CIVIQUE = Pattern.compile(
            "(?im)^\\s*\\d{1,5}\\s+(AV|AVE|RUE|BOUL|BLVD|CH|CHEMIN|ST|STREET)\\b.*$");

    // Remplace les identifiants sensibles (numéro de carte/contrat, folio, code
    // postal, ligne d'adresse civique) par [MASQUÉ] avant tout envoi à l'API
    // Claude. Le nom du titulaire n'est volontairement pas masqué ici - aucun
    // motif générique fiable pour un nom propre - seuls les identifiants
    // uniques et l'adresse le sont.
    public String masquer(String texte) {
        String resultat = texte;
        resultat = NUMERO_CARTE.matcher(resultat).replaceAll("[MASQUÉ]");
        resultat = NUMERO_CONTRAT.matcher(resultat).replaceAll("[MASQUÉ]");
        resultat = FOLIO.matcher(resultat).replaceAll("[MASQUÉ]");
        resultat = CODE_POSTAL.matcher(resultat).replaceAll("[MASQUÉ]");
        resultat = ADRESSE_CIVIQUE.matcher(resultat).replaceAll("[MASQUÉ]");
        return resultat;
    }
}