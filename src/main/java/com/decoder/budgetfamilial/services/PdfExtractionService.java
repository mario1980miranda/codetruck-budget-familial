package com.decoder.budgetfamilial.services;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class PdfExtractionService {

    // Extrait le texte du PDF avec tri par position (setSortByPosition) : sans ça,
    // les colonnes des tableaux de transactions ressortent éclatées (toutes les
    // dates, puis tous les montants, sans lien entre eux) - validé sur de vrais
    // relevés Desjardins avant d'écrire cette méthode.
    public String extraireTexte(MultipartFile fichier) throws IOException {
        try (PDDocument document = Loader.loadPDF(fichier.getBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            return stripper.getText(document);
        }
    }
}
