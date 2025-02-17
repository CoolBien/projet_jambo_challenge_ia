# Structuration

Class `Instance` : // chargée depuis un fichier
 - `jumbos` = `[(id, int, int), (id, int, int), ...]`
 - `items` = `[(id, int, int), (id, int, int), ...]`
 - `jumbosIdToName` = map de jumbo id -> jumbo name;
 - `itemsIdToName` = map de item id -> item name;

Class `Solution` : // Créée à partir de l'algorithme
 - `instance` = référence vers l'instance résolue;
 - `jumboCuts` = liste de `JumboCuts`;

Class `JumboCuts` : // Arbre de tous les `Cuts` d'un Jumbo.
 - `jumboId` = id du jumbo (utilisé pour l'exportation)
 - `cuts` = `BinaryTree<(VERTICAL, int sizeX, int sizeY, int itemFlipCoding, int... itemIds), ...>`;  (arbre)
   // Les items de l'arbre sont représentés par le record `Cut`, et l'orientation à l'intérieur par une valeur de l'énumération `CutOrientation` .
 - `cuttedResultSizes` = `[(int, int), (int, int), ...]`;   // (pour nous, pas dans le fichier de sortie).
 - `scraps` = `[(int, int), (int, int), ...]`;  // liste des chutes
 - `itemIds` = liste id items; // TODO

Class `Cut` : // Records des attributs d'un noeud de l'arbre cuts. 
 - `orientation` = orientation du cut; `enum CutOrientation : (HORIZONTAL, VERTICAL)`
 - `sizeX` = longueur horizontale du sous-jumbo sur lequel on effectue la découpe; `int` 
 - `sizeY` = hauteur verticale du sous-jumbo sur lequel on effectue la découpe; `int`
 - `itemFlipCoding` = Chaque bits du nombre binaire correspond à un item de la liste des items présents sur ce cut. Si le bit vaut 1, on retourne de 90° l'item, et s'il vaut 0 on n'y touche pas.
   exemple : `101001` = liste de 6 items, et on ne retourne que le 1er, le 4e et le 6e. `int`
 - `itemIds` = Liste des ids des items présents dans cette cut. `[int, int, ..., int]`

# Méthodes

## IO
Fonction lecture instance
Fonction ecriture solution

## PPC
Fonction partitionning avec PPC (en paramètre instance et en sortie liste indice/id des items pour chaque jumbo) pour reverifier apres ajout d'items non pris avant, recreer une instance intermediaire avec juste le jumbo et les items concernés
Fonction possibilite avec PPC (en paramètre liste items et taille bloc qui les contient et en sortie l'indice de l'item mis ou null)

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
    - Fonction algo génétique
    - Fonction recuperer items non pris
    - Noter items restants
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
