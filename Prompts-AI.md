
## Création de base de données avec le MCP de Neon platform

```text
Connecte-toi au MCP Neon pour configurer la base de données de ce projet.

Contexte :
- Projet Neon existant, id : billowing-darkness-45064033
- Nom de la base à créer : budget-familial-db

Étapes à suivre, dans l'ordre :

1. Utilise le MCP Neon pour créer la base de données `budget-familial-db`
   dans le projet `billowing-darkness-45064033` (branche par défaut).

2. Utilise l'utilisateur defaut de la platform `Neon`, soit le `neondb_owner` sur ce même
   projet, avec les droits nécessaires sur `budget-familial-db`, et récupère
   le mot de passe généré (ou la connection string complète) via le MCP.

3. Ouvre le fichier `.env` à la racine du projet.
   - S'il n'existe pas, crée-le à partir de `.env.example`.
   - S'il existe déjà avec du contenu, ne supprime QUE les lignes DB_URL,
     DB_USER, DB_PASSWORD — laisse le reste du fichier intact.

4. Remplis ces trois variables (noms exacts, ce sont ceux lus par
   application.yaml) :
   - DB_URL   : la chaîne JDBC Neon pour budget-familial-db, avec
     ?sslmode=require à la fin (format :
     jdbc:postgresql://<host>.neon.tech/budget-familial-db?sslmode=require)
   - DB_USER  : neondb_owner
   - DB_PASSWORD : le mot de passe récupéré à l'étape 2

5. Ne fais rien d'autre — pas de modification de pom.xml, application.yaml,
   ou du code Java dans cette tâche.

6. À la fin, confirme-moi simplement quelles variables ont été écrites dans
   .env (sans afficher le mot de passe en clair dans ta réponse).
```