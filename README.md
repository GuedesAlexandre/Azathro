# Azathro

Projet Balatro lite en java.
...

## Structure du projet

Le projet suit un modèle MVC (model view controller).

```
src/fr/uge/azathro/
├── Main.java
├── domain/
│   ├── types/               
|   │   ├── combination/
|   │   ├── rank/
|   │   └── suit/       
│   ├── Card.java      
│   ├── Blind.java       
│   ├── Deck.java      
│   ├── Hand.java        
│   └── HandEvaluator.java
├── model/
│   ├── GameState.java 
│   └── PlayerState.java
├── controller/
│   └── GameController.java
└── view/               
```

## Comment importer le projet

- Télécharger et décompresser l'archive .zip
- Ouvrir le projet dans Eclipse (open projects from file system).
- Vérifier que l'environnement d'exécution et la compilation sont bien en Java25


## Comment lancer le programme

- Sur Eclipse, lancer directement le programme sur Main.java (se trouvant dans src/fr.uge.azathro)

- En ligne de commande:

Compilation (se placer dans src/)
```
javac fr/uge/azathro/Main.java
```

Lancer le programme via la commande
```
java fr.uge.azathro.Main
```


## Implémentation

- représentation des cartes avec les combinaisons de poker
- une pioche de 52 cartes
- calcul correct du score des mains. Score cumulé à chaque main et réinitialisé à 0 à chaque début de blind
- boucle de jeu: 5 blinds constituées d'un nombre de mains limités, le jouer pioche 8 cartes et joue une main de 1 à 5 cartes à chaque tour. L'échec d'un blind met immédiatement fin au jeu.
- vue console fonctionnelle


## Amélioration

Interface graphique, extension
ajout de score par cartes
...