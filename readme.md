# Project Backlogs

API Spring Boot de gestion de backlogs projet avec intégration MCP (Model Context Protocol) pour l'assistance IA.

## Stack technique

- **Java 21** / Spring Boot 3.5.11
- **MongoDB** — base de données principale
- **Spring AI MCP Server** — exposition des outils via le protocole MCP (SSE)
- **Google Cloud AI Platform** (Vertex AI / Gemini) — assistance à la spécification
- **PlantUML** — génération de diagrammes
- **Spring Security** — authentification par API Key

---

## Lancer le projet

### Prérequis

- Java 21
- MongoDB accessible
- (Optionnel) Compte Google Cloud pour les features IA

### Variables d'environnement

| Variable           | Défaut                    | Description              |
| ------------------ | ------------------------- | ------------------------ |
| `MONGODB_HOST`     | `127.0.0.1`               | Hôte MongoDB             |
| `MONGODB_USER`     | `user`                    | Utilisateur MongoDB      |
| `MONGODB_PASSWORD` | `password`                | Mot de passe MongoDB     |
| `GCP_PROJECT`      | —                         | ID projet Google Cloud   |
| `GCP_LOCATION`     | `europe-west1`            | Région GCP               |
| `MY_API_KEY`       | `your-secret-api-key-here`| Clé API pour `/mcp/**`   |

### Démarrage local

```bash
./gradlew bootRun
```

L'application démarre sur le port **8080**.

### Docker

```bash
docker build -t project-backlogs .
docker run -p 8080:8080 \
  -e MONGODB_HOST=<host> \
  -e MONGODB_USER=<user> \
  -e MONGODB_PASSWORD=<password> \
  -e MY_API_KEY=<api-key> \
  project-backlogs
```

---

## Architecture du domaine

```text
Projet
 └── Acteur
      └── User Story
           └── Feature (hiérarchisable, typée)
                └── Tâche

Personne  ←── assignée à ──► Tâche
```

---

## API REST

### Projets (backlog)

| Méthode | Endpoint                                  | Description                    |
| ------- | ----------------------------------------- | ------------------------------ |
| GET     | `/project-command/all`                    | Liste tous les projets         |
| GET     | `/project-command/{project}/tree`         | Arbre complet du projet        |
| GET     | `/project-command/{project}/feature-tree` | Arbre des features             |
| GET     | `/project-command/{project}/csv`          | Export CSV acteurs/stories     |
| GET     | `/project-command/{project}/csv-tasks`    | Export CSV tâches              |
| POST    | `/project-command/{project}/add-actor`    | Ajoute un acteur               |
| POST    | `/project-command/{project}/add-raci`     | Ajoute une matrice RACI        |

### Tâches

| Méthode | Endpoint                          | Description                        |
| ------- | --------------------------------- | ---------------------------------- |
| POST    | `/task`                           | Crée une tâche                     |
| GET     | `/task`                           | Pagination, recherche et filtres   |
| GET     | `/task/{id}`                      | Détail d'une tâche                 |
| PUT     | `/task/{id}`                      | Met à jour une tâche               |
| DELETE  | `/task/{id}`                      | Supprime une tâche                 |
| GET     | `/task/by-project/{projectId}`    | Tâches d'un projet                 |
| GET     | `/task/by-status/{status}`        | Tâches par statut                  |
| GET     | `/task/overdue`                   | Tâches en retard                   |
| GET     | `/task/upcoming`                  | Tâches à venir (7 jours)           |
| GET     | `/task/by-tracking-ref/{ref}`     | Tâche par référence externe        |

Statuts suggérés : `TODO`, `IN_PROGRESS`, `DONE`, `BLOCKED`

### Personnes

| Méthode | Endpoint                  | Description                              |
| ------- | ------------------------- | ---------------------------------------- |
| POST    | `/person`                 | Crée une personne                        |
| GET     | `/person`                 | Liste paginée (`page`, `size`, `search`) |
| GET     | `/person/{id}`            | Détail d'une personne                    |
| GET     | `/person/email/{email}`   | Recherche par email                      |
| PUT     | `/person/{id}`            | Met à jour une personne                  |
| DELETE  | `/person/{id}`            | Supprime une personne                    |

### Features

| Méthode | Endpoint                                  | Description                    |
| ------- | ----------------------------------------- | ------------------------------ |
| GET     | `/feature-command/refresh-types`          | Recharge les types de features |
| POST    | `/feature-command/{story}/add`            | Ajoute une feature à une story |
| POST    | `/feature-command/{parent}/add-child`     | Ajoute une feature enfant      |
| POST    | `/feature-command/{parent}/adopt/{child}` | Adopte une feature existante   |

### Diagrammes

| Méthode | Endpoint                           | Description              |
| ------- | ---------------------------------- | ------------------------ |
| GET     | `/diagram/png/{name}`              | Diagramme en PNG         |
| GET     | `/diagram/plant-url/{name}`        | URL PlantUML             |
| GET     | `/diagram/plant-definition/{name}` | Définition PlantUML      |
| PATCH   | `/diagram/update/{name}`           | Met à jour la définition |

### Autres

| Méthode | Endpoint             | Description              |
| ------- | -------------------- | ------------------------ |
| GET     | `/featuretypes`      | Liste tous les types     |
| GET     | `/normalize-tasks`   | Normalise les références |

### Spring Data REST (auto-exposé)

Les repositories MongoDB sont exposés automatiquement :
`/projects`, `/tasks`, `/actors`, `/stories`, `/features`, `/peoples`, etc.

---

## MCP Server

Le serveur MCP est disponible via **SSE** sur `/mcp/sse`.

### Authentification MCP

Tous les endpoints `/mcp/**` nécessitent le header :

```http
X-API-Key: <valeur de MY_API_KEY>
```

### Outils MCP — Tâches

- `create-task`, `find-task-by-id`, `find-task-by-tracking-reference`
- `update-task`, `delete-task`
- `find-tasks` (pagination + recherche)
- `find-overdue-tasks`, `find-upcoming-tasks`
- `find-tasks-by-status`, `find-tasks-by-project`

### Outils MCP — Personnes

- `create-person`, `find-person-by-id`, `find-person-by-email`
- `update-person`, `delete-person`
- `find-persons` (pagination), `list-all-persons`

### Outils MCP — Projets

- `create-project`, `find-project-by-id`, `find-project-by-code`, `find-project-by-name`
- `update-project`, `delete-project`
- `find-projects` (pagination)

### Ressources MCP

Accessibles via `GET /mcp/resources?uri=<ressource>` :

```text
projects                          → liste des projets
projects/{id}                     → projet par ID
projects/{projectId}/tasks        → tâches d'un projet
tasks                             → liste des tâches
tasks/{id}                        → tâche par ID
tasks/by-tracking-ref/{ref}       → tâche par référence externe
tasks/by-status/{status}          → tâches par statut
tasks/overdue                     → tâches en retard
tasks/upcoming                    → tâches à venir
schemas/project                   → schéma JSON du projet
schemas/task                      → schéma JSON de la tâche
server/health                     → santé du serveur
metrics/projects/count            → nombre de projets
metrics/tasks/count               → nombre de tâches
```

---

## Types de features (`featuretypes.yml`)

Le fichier `featuretypes.yml` définit les types de features et leurs tâches habituelles générées automatiquement.

Exemples : `button`, `screen`, `form`, `field`, `list`, `rest-api`, `business-rule`, `function`, `procedure`, `rest-client`...

---

## Déploiement Cloud

L'application est configurée pour Google Cloud Run.
L'API publique est exposée via **Google Cloud Endpoints** :
`hypermanager-ia.endpoints.hypermanager.cloud.goog`

Sécurisée par API key (query `api_key` ou header `x-api-key`).
