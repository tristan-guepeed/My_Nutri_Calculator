# 🥗 MyNutriCalc – Application de Suivi Nutritionnel

> 🚧 **IMPORTANT : LA V1 DU BACKEND EST TERMINÉ ET FONCTIONNEL, MAIS LE FRONT-END EST EN COURS DE DÉVELOPPEMENT ET NON ACCESSIBLE POUR LE MOMENT !** 🚧

# Sommaire

- [Présentation du projet](#présentation-du-projet)
- [🚀 Prérequis](#🚀-prérequis)
  - [🛠 Outils nécessaires](#🛠-outils-nécessaires)
  - [💡 Sous Linux/Debian (exemple d'installation rapide)](#💡-sous-linuxdebian-exemple-dinstallation-rapide)
- [⚙️ Étapes de configuration](#⚙️-étapes-de-configuration)
  - [1. Créer la base de données et insérer l'utilisateur admin](#1-créer-la-base-de-données-et-insérer-lutilisateur-admin)
  - [2. 🍽 Alimenter la base avec des aliments et repas](#2-🍽-alimenter-la-base-avec-des-aliments-et-repas)
- [🔐 Admin par défaut](#🔐-admin-par-défaut)
- [📬 Endpoints utiles](#📬-endpoints-utiles)
  - [1. /foods – Gestion des aliments](#1-foods-–-gestion-des-aliments)
  - [2. /meals – Gestion des repas](#2-meals-–-gestion-des-repas)
  - [3. /diary – Journal alimentaire (suivi par date, utilisateur)](#3-diary-–-journal-alimentaire-suivi-par-date-utilisateur)
  - [4. /users – Gestion des utilisateurs & authentification](#4-users-–-gestion-des-utilisateurs--authentification)
- [📌 Historique des versions](📌-historique-des-versions)
- [Auteur](#auteur)


## Présentation du projet

Ce projet est une application développée avec Spring Boot, destinée à la gestion de la nutrition et du suivi alimentaire des utilisateurs. 

Elle permet aux utilisateurs de créer, modifier et consulter des repas personnalisés, de tenir un journal alimentaire quotidien, et de gérer leur profil utilisateur.

### Fonctionnalités principales

- **Gestion des utilisateurs** :  
  - Inscription et connexion sécurisées via JWT  
  - Mise à jour et suppression de compte  

- **Gestion des repas** :  
  - Création, modification, suppression et consultation des repas  
  - Gestion des ingrédients (aliments) et calcul des apports nutritionnels  

- **Journal alimentaire** :  
  - Enregistrement des repas consommés chaque jour dans un journal personnel  
  - Consultation des entrées par date  

- **Sécurité** :  
  - Spring Security pour restreindre l’accès selon le rôle (USER ou ADMIN)  
  - Contrôle d’accès basé sur l’appartenance des ressources  

## 🚀 Prérequis

Avant de lancer le projet, assure-toi d’avoir les outils suivants installés sur ta machine :

### 🛠 Outils nécessaires

| Outil                | Version minimale recommandée | Description                                                   |
|----------------------|------------------------------|---------------------------------------------------------------|
| **Java**             | 17+                          | Pour compiler et exécuter l’application Spring Boot            |
| **Maven**            | 3.6+                         | Pour construire le projet Java                                 |
| **MySQL**            | 8+                           | Base de données relationnelle utilisée par le backend         |
| **curl**             | -                            | Pour exécuter les appels API dans les scripts                 |
| **jq**               | -                            | Pour parser les réponses JSON dans les scripts bash           |
| **Apache utils (htpasswd)** | -                     | Pour générer un mot de passe hashé admin (utilisé dans les scripts de setup) |
| **bash**             | -                            | Interpréteur de script                                         |

---

## 💡 Sous Linux/Debian (exemple d'installation rapide)
```bash
sudo apt update && sudo apt install default-jdk maven mysql-server curl jq apache2-utils
```

---

##  ⚙️ Étapes de configuration

### 1. Créer la base de données et insérer l'utilisateur admin

Utilise le script suivant pour créer:
- La base **my_nutri_calc**
- L'utilisateur MySQL 'user'
- Un compte **admin** avec un mot de passe **adminpass** directement inséré en SQL
```bash
./start_config/create_admin.sh
```

💬 Le script te demandera de lancer l’application Java pour créer les tables avant d'insérer l’admin. Tu peux le faire via ton IDE ou en ligne de commande :
```bash
./mvnw spring-boot:run
```

### 2. 🍽 Alimenter la base avec des aliments et repas
Une fois le backend lancé, exécute ce script pour :
- Se connecter en tant qu'admin
- Créer une liste de **20** aliments courants
- Créer **6–7** repas types
```bash
./start_config/seed_data.sh
```

## 🔐 Admin par défaut
| Identifiant | Valeur                                 |
| ----------- | -------------------------------------- |
| Username    | `admin`                                |
| Password    | `adminpass`                            |
| UUID        | `d290f1ee-6c54-4b01-90e6-d701748f0851` |


## 📬 Endpoints utiles

Base URL : /api

### 1. /foods – Gestion des aliments
| Méthode | URL                           | Description                                                          | Autorisation                          |
| ------- | ----------------------------- | -------------------------------------------------------------------- | ------------------------------------- |
| GET     | `/foods/visible/{userId}` | Récupère la liste des aliments visibles par l'utilisateur et l'admin | USER ou ADMIN                         |
| POST    | `/foods/create`           | Crée un nouvel aliment                                               | USER ou ADMIN                         |
| PUT     | `/foods/update/{id}`      | Met à jour un aliment par son ID                                     | USER ou ADMIN (propriétaire ou admin) |
| DELETE  | `/foods/delete/{id}`      | Supprime un aliment par son ID                                       | USER ou ADMIN (propriétaire ou admin) |
| POST    | `/foods/{id}/image`       | Upload d’une image pour un aliment                                   | USER ou ADMIN (propriétaire ou admin) |
| DELETE  | `/foods/{id}/image`       | Supprime l’image d’un aliment                                        | USER ou ADMIN (propriétaire ou admin) |


### 2. /meals – Gestion des repas
| Méthode | URL                           | Description                                               | Autorisation                          |
| ------- | ----------------------------- | --------------------------------------------------------- | ------------------------------------- |
| GET     | `/meals/visible/{userId}` | Récupère les repas visibles par l’utilisateur et l’admin  | USER ou ADMIN                         |
| POST    | `/meals/create`           | Crée un nouveau repas avec ses items (aliment + quantité) | USER ou ADMIN                         |
| PUT     | `/meals/update/{id}`      | Met à jour un repas existant avec ses items               | USER ou ADMIN (propriétaire ou admin) |
| DELETE  | `/meals/delete/{id}`      | Supprime un repas                                         | USER ou ADMIN (propriétaire ou admin) |
| POST    | `/meals/{id}/image`       | Upload une image pour un repas                            | USER ou ADMIN (propriétaire ou admin) |
| DELETE  | `/meals/{id}/image`       | Supprime l’image d’un repas                               | USER ou ADMIN (propriétaire ou admin) |


### 3. /diary – Journal alimentaire (suivi par date, utilisateur)
| Méthode | URL                                  | Description                                                                   | Autorisation                          |
| ------- | ------------------------------------ | ----------------------------------------------------------------------------- | ------------------------------------- |
| POST    | `/api/diary/create`                  | Crée une entrée de journal (avec liste de repas)                              | USER ou ADMIN                         |
| PUT     | `/api/diary/update/{id}`             | Met à jour une entrée de journal                                              | USER ou ADMIN (propriétaire ou admin) |
| DELETE  | `/api/diary/delete/{id}`             | Supprime une entrée de journal                                                | USER ou ADMIN (propriétaire ou admin) |
| GET     | `/api/diary/all/{userId}`            | Récupère toutes les entrées du journal d’un utilisateur                       | USER ou ADMIN (propriétaire ou admin) |
| GET     | `/api/diary/{id}`                    | Récupère une entrée du journal par son ID                                     | USER ou ADMIN (propriétaire ou admin) |
| GET     | `/api/diary/by-date?date=YYYY-MM-DD` | Récupère les entrées du journal pour l’utilisateur connecté à une date donnée | USER ou ADMIN (utilisateur connecté)  |


### 4. /users – Gestion des utilisateurs & authentification
| Méthode | URL                   | Description                                   | Autorisation     |
| ------- | --------------------- | --------------------------------------------- | ---------------- |
| POST    | `/api/users/register` | Inscription d’un nouvel utilisateur           | Anonyme          |
| POST    | `/api/users/login`    | Authentification et obtention du token        | Anonyme          |
| PUT     | `/api/users/update`   | Mise à jour des infos du user connecté        | USER ou ADMIN    |
| DELETE  | `/api/users/delete`   | Suppression du compte utilisateur connecté    | USER ou ADMIN    |
| GET     | `/api/users/me`       | Récupérer les infos de l’utilisateur connecté | USER ou ADMIN    |
| GET     | `/api/users/{uuid}`   | Récupérer un utilisateur par son UUID         | ADMIN uniquement |

## 📌 Historique des versions

| Version | Date       | Description                                                                 |
|---------|------------|-----------------------------------------------------------------------------|
| 1.0.0   | 2025-08-01 | 🎉 Première version stable du backend : gestion complète des users, aliments, repas et journal. |

## Auteur

Tristan Beau
