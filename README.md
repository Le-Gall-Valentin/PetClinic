# Fonctionnalités implémentées
- 7.1.1 Edit Pet – correction: pré‑remplissage du type d’animal dans le formulaire d’édition (select cohérent avec le type existant), endpoints de mise à jour gérant typeId.
- 7.1.2 Vétérinaires CRUD: création, modification, consultation des détails, liste et suppression implémentés côté service et UI.
- 7.1.3 Associer un vétérinaire à une visite: champ facultatif vetId lors de l’ajout de visite; le vétérinaire apparaît dans les listes de visites.
- 7.1.4 Bonus RDV du vétérinaire: page de détails du vétérinaire affiche les visites, filtrables par période et type d’animal.
- 7.1.5 Bonus suppression de visite: suppression possible uniquement pour les visites futures; protection côté service et désactivation côté UI.
- 7.2.1 Suppression soft d’animaux: bouton de suppression logique (date de suppression) sans effacer les données; blocage si visites futures; OwnerDetails permet d’afficher/masquer les animaux supprimés.
- 7.2.2 Recherche clients améliorée: pagination serveur (20 par page), critères combinables AND (name, city, pet name), tri après pagination via entêtes de colonnes; requête effectuée côté serveur (Pageable/JPA).
- 7.2.4 Bonus suppression soft des vétérinaires: bouton de suppression logique (date), interdiction si visites futures; masquage des vétérinaires supprimés dans l’écran des vétérinaires et le formulaire d’ajout de visite; les visites historiques continuent d’afficher le vétérinaire supprimé.

# Répartition des tâches
- Valentin:
  - 7.1.1 Edit Pet: correction du select type dans [PetForm.vue](file:///c:/Users/valen/IdeaProjects/petclinic/spring-petclinic-api-gateway/src/main/frontend/src/views/PetForm.vue) et gestion typeId côté [PetController](file:///c:/Users/valen/IdeaProjects/petclinic/spring-petclinic-customers-service/src/main/java/org/springframework/samples/petclinic/customers/controller/pet/PetController.java).
  - 7.2.2 Recherche clients: pagination/tri/critères côté serveur via [OwnerController](file:///c:/Users/valen/IdeaProjects/petclinic/spring-petclinic-customers-service/src/main/java/org/springframework/samples/petclinic/customers/controller/owner/OwnerController.java) et UI [OwnersList.vue](file:///c:/Users/valen/IdeaProjects/petclinic/spring-petclinic-api-gateway/src/main/frontend/src/views/OwnersList.vue).
  - API Gateway: agrégations Owner/Vet et fallbacks dans [ApiGatewayController](file:///c:/Users/valen/IdeaProjects/petclinic/spring-petclinic-api-gateway/src/main/java/org/springframework/samples/petclinic/api/boundary/web/ApiGatewayController.java).
- Elia:
  - 7.1.2 Vets CRUD: endpoints et pages [VetController](file:///c:/Users/valen/IdeaProjects/petclinic/spring-petclinic-vets-service/src/main/java/org/springframework/samples/petclinic/vets/controller/vet/VetController.java), [VetList.vue](file:///c:/Users/valen/IdeaProjects/petclinic/spring-petclinic-api-gateway/src/main/frontend/src/views/VetList.vue), [VetForm.vue](file:///c:/Users/valen/IdeaProjects/petclinic/spring-petclinic-api-gateway/src/main/frontend/src/views/VetForm.vue).
  - 7.1.3 Associer vet à visite & 7.1.5 suppression visite future: UI [Visits.vue](file:///c:/Users/valen/IdeaProjects/petclinic/spring-petclinic-api-gateway/src/main/frontend/src/views/Visits.vue) et règles backend [VisitServiceImpl](file:///c:/Users/valen/IdeaProjects/petclinic/spring-petclinic-visits-service/src/main/java/org/springframework/samples/petclinic/visits/service/visit/VisitServiceImpl.java).
  - 7.1.4 Détails Vet + filtres: page [VetDetails.vue](file:///c:/Users/valen/IdeaProjects/petclinic/spring-petclinic-api-gateway/src/main/frontend/src/views/VetDetails.vue) et agrégation côté Gateway.
  - 7.2.1/7.2.4 Suppression soft Pet/Vet: modèles, services et filtres UI ([Pet.java](file:///c:/Users/valen/IdeaProjects/petclinic/spring-petclinic-customers-service/src/main/java/org/springframework/samples/petclinic/customers/model/pet/Pet.java), [Vet.java](file:///c:/Users/valen/IdeaProjects/petclinic/spring-petclinic-vets-service/src/main/java/org/springframework/samples/petclinic/vets/model/vet/Vet.java), [OwnerDetails.vue](file:///c:/Users/valen/IdeaProjects/petclinic/spring-petclinic-api-gateway/src/main/frontend/src/views/OwnerDetails.vue), [Visits.vue](file:///c:/Users/valen/IdeaProjects/petclinic/spring-petclinic-api-gateway/src/main/frontend/src/views/Visits.vue)).

# Choix de conception
- Microservices et agrégation au Gateway: la composition Owner/Vet se fait côté Gateway pour limiter le couplage et centraliser la résilience (CircuitBreaker/Retry + fallbacks).
- Soft delete (Pet/Vet): conservation des données historiques (comptable), via champ `deletionDate` et filtrage UI; règles serveur interdisent la suppression si visites futures pour garantir l’intégrité.
- Recherche Owners côté serveur: Pageable (20), critères AND (name/city/pet), tri appliqué après pagination pour des listes volumineuses; évite le filtrage client non scalable.
- Association vet→visite: `vetId` facultatif pour ne pas contraindre la prise de rendez‑vous; contrôles backend si vet supprimé.
- Filtres VetDetails: paramètres from/to + type d’animal appliqués côté service et UI pour une UX précise sans surcharge réseau.
- Découverte via Eureka et routes lb://: ports dynamiques en dev, stabilité en docker; configuration centralisée via Config Server (profil `native`).

# Difficulés rencontrées et solutions
- Nouveauté microservices: coordination Config/Eureka/Gateway, propagation d’erreurs et timeouts, latences entre services; solution: ordre de démarrage strict, circuit breakers et fallbacks, observabilité (Eureka/Actuator).
- Agrégations distribuées: joindre owners/pets/visits/vets côté Gateway sans transactions distribuées; solution: appels parallèles, mappings par `petId`/`vetId`, valeurs par défaut en cas d’indispo.
- Cohérence soft delete: s’assurer que les éléments supprimés logiquement restent visibles là où requis (listes de visites) et masqués ailleurs; solution: filtres UI + validations serveur (visites futures).
- Recherche paginée/tri: gérer tri après pagination côté serveur pour conserver l’ordre entre pages; solution: Pageable + Sort appliqués au repository, UI envoie colonnes et direction.
- Édition Pet type: synchroniser typeId entre UI et backend pour le pré‑remplissage; solution: charger `petTypes` avant rendu, set `selectedType` à partir du `pet.type`.
- Suppression visite future: éviter suppression passée/présente; solution: validation date côté service et désactivation du bouton côté UI.
