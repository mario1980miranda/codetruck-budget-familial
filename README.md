# Budget Familial

Monorepo pour suivre le budget du foyer : import de relevés bancaires PDF,
catégorisation automatique par IA (Claude Haiku), agrégation des dépenses
par catégorie, par période, par compte et par type de compte, et une
interface web pour consulter tout ça.

```text
backend/    API REST Spring Boot (Java 25)
frontend/   Interface Vue 3 + Vite + TypeScript
docs/       Collection Postman, prompts
```

## Démarrage rapide (tout en Docker, base locale)

Prérequis : Docker avec Docker Compose, et une clé API Anthropic.

```bash
cp .env.example .env        # puis renseigner ANTHROPIC_API_KEY
docker compose up -d --build --wait
```

Les services démarrent dans l'ordre, et chacun attend que le précédent soit OK :

1. **postgres** : attend que `pg_isready` réponde.
2. **backend** : attend que `GET /sante` réponde `{"statut":"OK"}`.
3. **frontend** : attend que `/api/sante` réponde à travers nginx.

`--wait` rend la main quand les trois sont « healthy », ou échoue sinon.

| Service | URL |
|---|---|
| Application | http://localhost:8081 |
| API (Postman) | http://localhost:8080 (en-tête `X-API-VERSION: v1`) |
| Postgres | `localhost:5433`, base / utilisateur / mot de passe : `budget_familial` |

```bash
docker compose ps                       # état des services
docker compose logs -f backend          # logs du backend
docker compose down                     # arrêter (les données sont conservées)
docker compose down -v                  # arrêter ET vider la base de données
```

Après `down -v`, le prochain `up` repart d'une base vide ; Hibernate recrée les tables.
Après une modification du code, relancer `docker compose up -d --build --wait`.

## Backend

* CRUD `Comptes` (`/comptes`) — chacun des comptes du foyer
  (compte courant / carte de crédit, par titulaire, plus le compte conjoint).
* `POST /comptes/{compteId}/releves` — import d'un relevé PDF (extraction PDFBox,
  masquage des données sensibles, catégorisation par Claude Haiku).
* `GET /agregations` — totaux revenus / dépenses / épargne et dépenses par catégorie.
* `GET /transactions` — transactions de la période.
* Filtres de `/agregations` et `/transactions` : `debut`, `fin` (obligatoires),
  `titulaire`, `compteId`, `typeCompte` (`COURANT` ou `CARTE_CREDIT`), et
  `categorie` pour `/transactions`.
* Toutes les requêtes exigent l'en-tête `X-API-VERSION: v1`.

### Démarrage

1. Copier `backend/.env.example` en `backend/.env` et remplir avec tes identifiants Neon.
2. `cd backend && ./mvnw spring-boot:run`
3. Vérifier : `curl -H "X-API-VERSION: v1" http://localhost:8080/sante`

### Base de données locale (dev)

Pour développer le backend contre le Postgres de docker-compose (exposé sur le port 5433) :

```bash
docker compose up -d postgres
cd backend && ./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

## Frontend

Prérequis : Node.js 22+.

```bash
cd frontend
npm install
npm run dev      # http://localhost:5173
```

En développement, Vite redirige `/api/*` vers `http://localhost:8080` : le
backend doit tourner en parallèle.

Pages :

* **Tableau de bord** (`/`) — totaux et graphique des dépenses par catégorie.
* **Transactions** (`/transactions`) — tableau des transactions filtrées.
* **Comptes** (`/comptes`) — liste des comptes, avec un lien pour importer un relevé et un lien vers le tableau de bord du compte.
* **Nouveau compte** (`/comptes/nouveau`) — création d'un compte, puis redirection vers l'import.
* **Importer** (`/import`) — envoi d'un relevé PDF sur un compte (extraction, masquage,
  catégorisation par IA) et affichage des transactions importées.

Pour tester avec ses propres données sur une base vide : **Comptes → Nouveau compte**,
puis importer un relevé PDF par compte.
