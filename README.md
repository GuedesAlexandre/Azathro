# Azathro

Projet Balatro lite en java.

## Structure du projet

Le projet suit un modèle MVC (model view controller).

```
src/fr/uge/azathro/
├── Main.java
├── domain/
│   ├── types/               
|   │   ├── Card.java
|   │   ├── Rank.java       
|   │   ├── Suit.java        
│   ├── Combination.java 
│   ├── Planet.java     
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

- Sur Eclipse, lancer directement le programme sur Main.java (src/fr.uge.azathro)


- En ligne de commande:

Compilation
```
javac fr/uge/azathro/domain/*.java 
javac fr/uge/azathro/model/*.java
javac fr/uge/azathro/view/*.java
javac fr/uge/azathro/controller/*.java
javac fr/uge/azathro/Main.java
```

Lancer le programme via la commande
```
java fr.uge.azathro.Main
```


## Implémentation

- représenter les cartes avec les combinaisons de poker et les blinds


## Amélioration

Interface graphique, extension
ajout de score par cartes
