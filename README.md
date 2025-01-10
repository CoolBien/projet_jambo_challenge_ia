# Structuration

Class `Instance` :
 - jumbos = `[(id, int, int), (id, int, int), ...]`
 - items = `[(id, int, int), (id, int, int), ...]`

Class `Solution` :
 - liste de `JumboCuts`

Class `JumboCuts` :
 - decoupes = `[(VERTICAL, int), (HORIZONTAL, int),...]`
 - taille resultats decoupes = `[(int, int), (int, int), ...]`   // (pour nous, pas dans le fichier de sortie)
 - liste id items

# Méthodes

## IO
Fonction lecture instance
Fonction ecriture solution

## PPC
Fonction partitionning avec PPC (en paramètre instance et en sortie liste indice/id des items pour chaque jumbo) pour reverifier apres ajout d'items non pris avant, recreer une instance intermediaire avec juste le jumbo et les items concernés
Fonction possibilite avec PPC (en paramètre liste items et taille bloc qui les contient et en sortie booleen faisable ou non)

## Algo génétique
Fonction algo génétique (en paramètre instance et en sortie solution) pour faire génétique sur chutes et items restants, refaire une instance avec en jumbo les chutes
 - Individu = Liste de découpe pour un jumbo = une solution possible pour découper un jumbo
 - Evaluation --> Score = Valeur de l'aire des résidus. Plus l'aire des pertes est petite, plus le score est élevé.
 - Tournoi pour choisir le meilleur jumbo = choisir celui avec le moins de pertes, le score le plus élevé a plus de probabilité de gagner le tournoi. Les probabilités seront donc pondérées par rapport au score des 2 individus sélectionnés pour le tournoi.
 - Croisement = Un enfant hérite d'un parent le gène le plus avantageux, ici son score
 - Mutation = On inverse aléatoirement des coordonnées de découpe, ou on en modifie quelques unes.
 - On choisit une population avec 10 individus d'abord, ou 20 si ça n'est pas suffisant

## Utils
Fonction récuperer la taille de chute de chaque jumbo (en paramètre la solution et en sortie liste des chutes)
Fonction recuperer items non pris (en paramètre solution et en sortie liste indice des items non pris)

Fonction trouver le jumbo avec le plus de chute (en paramètre solution et en sortie indice/id jumbo)
Fonction réorganiser dernier jumbo (en paramètre JumboCuts du dernier jumbo, nouveau JumboCuts)

## Résolution
Fonction resoudre :
 - Fonction partitionning
 - Pour tout jumbo :
    - Si pas premier jumbo, vérifier que ajout items toujours faisable
    - Fonction algo génétique
    - Fonction recuperer items non pris
    - Si pas dernier jumbo, placer items dans prochain jumbo
    - Si dernier jumbo, noter items restants
 - Retourne solution

## Algo:
Fonction de l'Algo :
 - Fonction resoudre
 - Fonction recuperer items non pris
 - Fonction recuperer tailles chutes
 - Creer instance avec items restants et chutes
 - Fonction resoudre avec nouvelle instance
 - Rajouter coupes trouvées dans la solution
 - Si reste items les mettres dans le plus grand jumbo encore dispo (fonction resoudre avec objectif de faire une grosse chute)
 - Sinon fonction trouver jumbo avec plus de chute et reorganiser

## Main
Fonction `main()` :
 - Lire instance
 - Exécuter l'algo
 - Checker solution
 - Ecrire solution
