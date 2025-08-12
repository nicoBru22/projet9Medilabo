#**Nom du projet**

Medilabo

##**Langage**

Les langages utilisés :
- Backend : Java avec Springboot 
- Frontend : Javascript avec React

##**Installation du projet**

Pour installer le projet, il est nécessaire d'installer les packages de chaque API et de la gateway. Dans chaque élément, vous pouvez utiliser le terminal avec la commance :  mvn clean package.

##**Lancer le projet**

Ensuite, pour démarrer le projet, il faut aller à la racine du projet medilabo, où se trouve le Docker-compose, lancer le terminal avec la commande : docker compose up --build.

#**Architecture du projet**

Le projet est composé de 5 micro services : 
- patient, 
- utilisateur, 
- notes, 
- alerte, 
- gateway, 
Ainsi que d'un front.

#**Microservice Patient**

Le microservice patient permet de faire les opérations CRUD sur l'objet Patient. 

Objet Patient : 
- id : Long
- prenom : String
- nom : String
- dateNaissance : Date
- adresse : String
- telephone : String
- genre : String
- dateCreation : LocalDate
- dateModification : LocalDate

Base de donnée
- PostgreSql (SQL).
- H2 Database, pour les tests.

Architecture :
- controller
- service (implementant une interface)
- repository
- model

#**Microservice Utilisateur**

Le microservice utilisateur permet les opérations CRUD sur l'objet Utilisateur, ainsi que la gestion de l'authentification.

Objet User : 
- id : Long
- username : String
- nom : String
- prenom : String
- role : String
- password : String

Base de donnée :
- postgreSql (SQL)
- H2 Database, pour les tests.

Architecture :
- controller
- service (implementant une interface)
- repository
- model
- configuration de sécurité

Sécurité : 
- Utilisation de Spring Security et authentification manager. 
- Utilisation de token avec Json Web Token
- Le JWT contient : id, username, nom, prenom, role.
- Le token est nécessaire pour toutes les requêtes venant du front vers la gateway.

#**Microservice Note**

Le microservice note permet les opérations CRUD sur l'objet Note.

Objet Note : 
- id : String
- patientId : Long
- userId : Long
- note : String

Le patientId est une clé étrangère en direction de la table Patient. Le userId est une clé étrangère en direction de la table Utilisateur.

Base de donnée : 
- MongoDB (NoSql)
- TestContainer pour les tests

Architecture :
- controller
- service (implementant une interface)
- repository
- model

Sécurité : 
- le Jwt est mise en place afin de pouvoir extraire l'id, le nom et le prénom de l'utilisateur afin de les voir s'afficher dans le front pour une transmission donnée.
- Ajout de note est accessible à partir de la page information du patient.

#**Microservice Alerte**

Le microservice alerte permet d’analyser les données d’un patient et ses notes pour détecter un risque de diabète.

Architecture : 
- controller
- service (implementant une interface)
- model

Ce risque de diabète peut être : 
- none, 
- borderline, 
- inDanger,
- EarlyOnset.

Fonctionnement : 
- Utilisation de feign pour appeler les microservices patient et notes
- Une liste de mots clé recherché dans les notes est visible dans le service
- Prend en compte l'age et le genre du patient

Pour réaliser ce traitement, ce service nécessite l'utilisation de feign pour faire des appels sur les microservices patient et notes.

#**Gateway**

La gateway est le point d'entrée de l'application. 

Fonctionnalité : 
- Reçoit les requêtes du front
- Filtre et distribue vers le microservice concerné
- Filtrage via Spring Security et JWT

#**CI/CD**


#**Contenerisation** 

Utilisation de Dockerfiles sur chaque microservice et d un docker compose à la racine


