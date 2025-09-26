# PayFlow – Gestion centralisée des abonnements

## Description du projet

**PayFlow** est une application console Java 8 qui centralise la gestion des abonnements personnels et professionnels (streaming, logiciels, assurances, etc.).  
Elle permet de suivre les échéances, détecter rapidement les paiements manqués et générer des rapports financiers synthétiques pour mieux anticiper et contrôler son budget.

## Objectifs

- Centraliser la gestion de tous vos abonnements (avec ou sans engagement)
- Suivre les paiements, échéances et impayés
- Générer des rapports financiers mensuels et annuels
- Anticiper le budget et éviter les oublis de paiement

## Technologies utilisées

- Java 8 (JDK 1.8)
- JDBC (PostgreSQL ou MySQL)
- Java Time API
- UUID
- Programmation fonctionnelle (Stream API, lambda, Optional, Collectors)



## Structure du projet

- `src/io/github/alirostom1/payflow/entity` : Entités métier (`Abonnement`, `AbonnementAvecEngagement`, `AbonnementSansEngagement`, `Paiement`)
- `src/io/github/alirostom1/payflow/service` : Logique métier (`SubscriptionService`, `PaymentService`)
- `src/io/github/alirostom1/payflow/ui` : Interface console (menus, navigation)
- `src/io/github/alirostom1/payflow/repository` : DAO/JDBC pour la persistance (`AbonnementDAO`, `PaiementDAO`)
- `src/io/github/alirostom1/payflow/util` : Utilitaires (gestion des dates, validations, scheduler)

## Modèle de données

- **Abonnement** :  
  - id (UUID), nomService, montantMensuel, dateDebut, dateFin, statut (Active, Suspendu, Résilié), typeAbonnement, duréeEngagementMois
- **Paiement** :  
  - idPaiement (UUID), idAbonnement, dateEcheance, datePaiement, typePaiement, statut (Payé, Non payé, En retard)
- **Relation** : 1 abonnement → n paiements

## Fonctionnalités principales

- Créer, modifier, supprimer un abonnement (avec/sans engagement)
- Générer automatiquement les échéances de paiement
- Enregistrer, modifier, supprimer un paiement
- Détecter et afficher les paiements manqués (impayés) avec le montant total dû
- Afficher la somme payée d’un abonnement
- Afficher les 5 derniers paiements
- Générer des rapports financiers (mensuels, annuels, impayés)
- Navigation simple via menus console

## Exigences techniques

- Application console Java 8
- Persistance via JDBC (PostgreSQL ou MySQL)
- Architecture en couches (UI, services, DAO, utilitaires)
- Gestion des exceptions (try/catch, messages clairs)
- Utilisation avancée de Stream API et Collectors
- Contrôle de version avec Git

## Prérequis

- JDK 8 installé
- PostgreSQL ou MySQL opérationnel
- Git

## Exemple d’exécution

```bash
java -cp "dist/payflow.jar:src/io/github/alirostom1/payflow/drivers/mysql-connector-j-9.3.0.jar" io.github.alirostom1.payflow.Main
```

---

**Diagramme de classe**
![class diagram](https://github.com/alirostom1/PayFlow/blob/develop/docs/diagrams/payflow.png)
---
