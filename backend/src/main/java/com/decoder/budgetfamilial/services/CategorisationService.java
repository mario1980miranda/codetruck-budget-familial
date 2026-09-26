package com.decoder.budgetfamilial.services;

import com.decoder.budgetfamilial.dtos.TransactionExtraiteDto;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class CategorisationService {

    private static final String INSTRUCTIONS = """
            Tu reçois le texte extrait d'un relevé bancaire Desjardins (carte de crédit
            ou compte courant). Retourne la liste des transactions réelles en respectant
            strictement ces règles.

            FORMAT CARTE DE CRÉDIT (tableau "DESCRIPTION DES TRANSACTIONS COURANTES") :
            - Chaque ligne du tableau de transactions est une DEPENSE.
            - Une ligne qui commence par un montant suivi de "DOLLAR AMERICAIN TX:" n'est
              PAS une transaction : c'est le montant en devise d'origine de la transaction
              juste au-dessus. Ignore-la.
            - Un montant suivi de "CR" collé sans espace (ex: "2 693,22CR") est un paiement
              de la carte : type TRANSFERT_INTERNE, jamais DEPENSE.
            - Tout ce qui suit "REMISES EN ARGENT ACCUMULÉES" n'est pas une liste de
              transactions (résumé de cashback par catégorie) : ignore-le entièrement.

            FORMAT COMPTE COURANT (tableau "COMPTE D'OPÉRATIONS COURANTES") :
            - La ligne "Solde reporté" en tête n'est pas une transaction : c'est le solde
              de départ, sers-t'en uniquement de référence pour le calcul ci-dessous.
            - Chaque ligne a un seul montant avant le solde (colonnes Retrait et Dépôt
              fusionnées à l'extraction) : détermine DEPENSE vs REVENU en comparant le
              solde de cette ligne à celui de la ligne précédente. Solde qui baisse =
              DEPENSE. Solde qui monte = REVENU (sauf exceptions ci-dessous).
            - Une ligne "Retrait-virement de / FIDUCIE DESJARDINS" est un virement vers de
              l'épargne : type EPARGNE, jamais DEPENSE.
            - Une ligne "Paiement facture ... Desjardins Remises World Elite" (paiement de
              carte de crédit) ou "Virement - AccèsD Internet / à <numéro de compte>" est
              un mouvement entre comptes personnels : type TRANSFERT_INTERNE.
            - Arrête-toi dès que tu rencontres "COMPTE D'EPARGNE ET DE PLACEMENT" : tout ce
              qui suit n'est pas à extraire pour cette version.
            - Une description peut continuer sur la ligne suivante avant les montants
              (ex: "Paiement facture - AccèsD Internet / Desjardins Remises" puis "World
              Elite" sur la ligne d'après) : recolle-la en une seule description.

            DATES : les lignes de transaction n'ont que jour/mois, jamais l'année. Trouve
            l'année dans l'en-tête du relevé (date du relevé ou période couverte) et
            applique-la à chaque transaction. Si le mois d'une transaction est après le
            mois de clôture du relevé, elle appartient à l'année précédente.

            MONTANTS : convertis en nombre décimal standard, point comme séparateur, sans
            espace ni virgule (ex: "1 361,57" devient 1361.57). Le montant est toujours une
            valeur positive - le signe est porté par typeTransaction, pas par le montant.

            CATÉGORIE : uniquement pour les transactions de type DEPENSE, choisis la
            catégorie la plus plausible depuis le nom du commerçant. Pour REVENU, EPARGNE
            et TRANSFERT_INTERNE, laisse la catégorie à null.

            Ne devine pas une catégorie par ressemblance de nom si tu n'es pas sûr du
            commerçant - utilise AUTRE plutôt qu'une catégorie hasardeuse. Exemples de
            pièges : FIZZ est un fournisseur de téléphonie mobile (pas TRANSPORT).
            L'Aubainerie est un magasin de vêtements (pas LOGEMENT). Un abonnement logiciel
            ou de streaming (Google, LastPass, YouTube, etc.) est généralement AUTRE, sauf
            s'il s'agit clairement de LOISIRS (divertissement).
            Assurance habitation ou assurance vie/santé - AUTRE, sauf si le nom mentionne
            explicitement "habitation" ou "condo" - alors LOGEMENT.

            Ignore tout texte d'en-tête/pied de page répété (adresse, numéro de compte,
            numéro de folio, "Page X de Y", taux d'intérêt, limites de crédit).

            Texte du relevé :
            %s
            """;

    private final ChatClient chatClient;
    private final PdfExtractionService pdfExtractionService;
    private final MasquageService masquageService;

    public CategorisationService(ChatClient.Builder chatClientBuilder,
                                 PdfExtractionService pdfExtractionService,
                                 MasquageService masquageService) {
        this.chatClient = chatClientBuilder.build();
        this.pdfExtractionService = pdfExtractionService;
        this.masquageService = masquageService;
    }

    // Extrait le texte du PDF, masque les identifiants sensibles, puis demande à
    // Claude Haiku de structurer les transactions selon les règles ci-dessus,
    // propres à chaque format de relevé Desjardins.
    public List<TransactionExtraiteDto> categoriserReleve(MultipartFile fichier) throws IOException {
        String texteBrut = pdfExtractionService.extraireTexte(fichier);
        String texteMasque = masquageService.masquer(texteBrut);
        String prompt = INSTRUCTIONS.formatted(texteMasque);

        return chatClient.prompt()
                .user(prompt)
                .call()
                .entity(new ParameterizedTypeReference<List<TransactionExtraiteDto>>() {
                });
    }
}