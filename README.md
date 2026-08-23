# Budget Familial API

Service REST pour suivre le budget du foyer : import de relevés bancaires PDF,
catégorisation automatique par IA (Claude Haiku), et agrégation des dépenses
par catégorie, par période et par compte.

## Étape 1 — Setup (fait)

* Projet Spring Boot + PostgreSQL (Neon) connecté.
* CRUD `Comptes` (`/comptes`) — représente chacun des comptes du foyer
  (compte quotidien / carte de crédit, par titulaire, plus le compte conjoint).
* Endpoint santé (`/sante`).

## Étape 2 (actuelle) — Extraction PDF

* `PdfExtractionService` : extrait le texte d'un PDF avec PDFBox
  (`setSortByPosition(true)` — indispensable pour garder les colonnes des
  tableaux de transactions alignées).
* `POST /extraction/texte` (multipart, champ `fichier`) — retourne le texte
  brut extrait. Rien n'est encore persisté ni envoyé à l'IA à cette étape.

## Démarrage

1. Copier `.env.example` en `.env` et remplir avec tes identifiants Neon.
2. `./mvnw spring-boot:run`
3. Vérifier : `curl -H "X-API-VERSION: v1" http://localhost:8080/sante`

## Prochaines étapes

2. Extraction du texte des PDF (Apache PDFBox).
3. Structuration des transactions par IA (Spring AI + Claude Haiku).
4. Persistance des relevés et transactions.
5. Agrégation par catégorie / période / compte.
