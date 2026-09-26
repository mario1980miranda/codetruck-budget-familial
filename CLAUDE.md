# Budget Familial (2026)
Monorepo : un service REST (`backend/`) pour suivre les dépenses du foyer par catégorie et par compte, à partir de relevés bancaires PDF importés et catégorisés par IA, et une interface Vue (`frontend/`) qui affiche comptes, totaux et transactions par période.

## Stack
* Backend : Java 25, Spring Boot 4.1, Maven
* PostgreSQL sur Neon. Schéma généré par Hibernate ddl-auto: update. Pas d'outil de migration.
* Spring AI avec Claude Haiku (Anthropic) pour la catégorisation — ajouté à l'étape 3
* Apache PDFBox pour l'extraction du texte des PDF — ajouté à l'étape 2
* Frontend : Vue 3 + Vite + TypeScript, Vue Router, Chart.js (vue-chartjs)
* Tout le code et les identifiants en français

## Commandes
* Backend (depuis `backend/`, où se trouve `.env`) :
  * Lancer: `./mvnw spring-boot:run`
  * Build: `./mvnw -q verify`
* Frontend (depuis `frontend/`) :
  * Lancer: `npm run dev` (http://localhost:5173, proxy `/api` → backend :8080)
  * Build: `npm run build` (vérification des types + build)
* Tout en Docker (depuis la racine, `.env` avec `ANTHROPIC_API_KEY`) :
  * Lancer: `docker compose up -d --build --wait` (postgres → backend → frontend, chacun attend le healthcheck du précédent ; app sur http://localhost:8081)
  * Vider la base locale: `docker compose down -v`
  * Postgres local exposé sur le port 5433 (profil Spring `local`)

## Nommage
* Tables: tb_<nom_pluriel> - ex. tb_comptes
* Entités: <Nom>Modele - ex. CompteModele
* DTOs d'entrée: <Nom>EnregistrementDto en Java records - ex. CompteEnregistrementDto
* Il n'y a PAS de DTOs de sortie. Les contrôleurs retournent l'entité directement.

## Tests
* Écrire un test unitaire pour chaque nouvelle méthode de service.
* Ne pas écrire de tests d'intégration sauf si demandé.

## Règles strictes
* Utiliser BeanUtils.copyProperties (choisi pour la simplicité de ce projet d'apprentissage - pas MapStruct).
* La logique métier vit dans la couche service. Les contrôleurs ne font que traduire HTTP vers appels de service et retour.
* Ne pas inclure de logging sauf si explicitement demandé.
* Ne jamais changer ddl-auto autrement que update.
* Utiliser des identifiants de type UUID.
* Après chaque changement de code backend, lancer `./mvnw -q verify` (dans `backend/`) avant de considérer la tâche terminée ; après un changement frontend, `npm run build` (dans `frontend/`).
* Ne jamais envoyer à l'API Claude un numéro de compte, de contrat ou une adresse en clair — masquer ces motifs dans le texte extrait avant l'appel (voir étape 2/3).
* Les virements entre comptes personnels (paiement de carte de crédit, virement compte à compte) sont catégorisés TRANSFERT_INTERNE et exclus des totaux de dépenses/épargne.

## Garde-fous
Un hook `PreToolUse` (`.claude/hooks/guardrails.sh`, câblé dans `.claude/settings.json`) s'exécute avant chaque `Edit/Write`. Sur les fichiers `.java`, il bloque exactement une chose :

* @Autowired seul sur sa ligne (injection par champ)

Si une modification est bloquée, corrige le code - ne contourne pas le hook.

Un blocage correspond à une sortie terminale de code 2 avec le message suivant, émis par `guardrails.sh` :

> "Violation de convention dans <chemin_de_la_classe_java>: Injection par champ - utiliser l'injection par constructeur."

## Mode explication différée
Ce code est pour l'auto-apprentissage et la pratique ; il est possible que j'aie besoin de l'expliquer à quelqu'un d'autre plus tard.
* Générer UN seul fichier à la fois sauf si demandé autrement.
* Préférer clair et explicite plutôt que malin ou compact.
* Toute méthode avec une `boucle`, une `branche conditionnelle`, ou `plus d'un appel à un collaborateur`, commenter ce que fait le code avant son annotation (quand elle existe) et avant sa déclaration. NE JAMAIS commenter à l'intérieur des lignes de la méthode sauf si demandé.
* Ne pas ajouter de fonctionnalités qui n'ont pas été demandées.

## Frontend
* Composants en `<script setup lang="ts">` (Composition API).
* Tous les appels HTTP passent par `frontend/src/api/` (via `client.ts`, qui ajoute l'en-tête `X-API-VERSION: v1`) — jamais de `fetch` dans les composants.
* Les types de `frontend/src/types/budget.ts` sont le miroir des enums et des objets JSON du backend : les mettre à jour quand le backend change.

## Disposition
```text
backend/src/main/java/com/decoder/budgetfamilial/
  controllers/   Contrôleurs REST
  services/      Règles métier
  repositories/  Interfaces Spring Data JPA
  models/        Entités JPA
  dtos/          Records d'entrée
  configs/       Configuration Spring
frontend/src/
  api/           Appels au backend
  types/         Types miroir du backend
  components/    Composants réutilisables (filtres, cartes, graphique, tableau)
  views/         Pages (tableau de bord, transactions, comptes)
  router/        Routes
  utils/         Formatage (montants, dates)
docs/            Collection Postman, prompts
```
