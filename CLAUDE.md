# Budget Familial API (2026)
Un service REST pour suivre les dépenses du foyer par catégorie et par compte, à partir de relevés bancaires PDF importés et catégorisés par IA.

## Stack
* Java 25, Spring Boot 4.1, Maven
* PostgreSQL sur Neon. Schéma généré par Hibernate ddl-auto: update. Pas d'outil de migration.
* Spring AI avec Claude Haiku (Anthropic) pour la catégorisation — ajouté à l'étape 3
* Apache PDFBox pour l'extraction du texte des PDF — ajouté à l'étape 2
* Tout le code et les identifiants en français

## Commandes
* Lancer: `mvn spring-boot:run`
* Build: `mvn -q verify`

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
* Après chaque changement de code, lancer `mvn -q verify` avant de considérer la tâche terminée.
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

## Disposition
```text
src/main/java/com/decoder/budgetfamilial/
controllers/   Contrôleurs REST
services/      Règles métier
repositories/  Interfaces Spring Data JPA
models/        Entités JPA
dtos/          Records d'entrée
configs/       Configuration Spring
```
