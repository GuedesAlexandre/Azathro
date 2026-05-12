# Azathro

Projet Balatro lite en java.
TODO...


## Structure du projet

Le projet suit un modèle MVC (model view controller).

```
src/
|-- domain/             # objets métier purs
| |--types/
  |   |--Combination.java
  |   |--Suit.java
  |   `--Rank.java
| |-- Card.java
| |-- Blind.java
| |-- ...
|-- model/          # état du jeu
| |-- GameState.java
| `-- ...
|-- controller/         # logique de jeu
| `-- GameController.java
|-- view/           # affichage
| |-- View.java     # interface
| `-- ...
`-- Main.java
```


## Comment importer le projet

- Télécharger le fichier .zip et l'ouvrir dans Eclipse (open projects from file system).
- Vérifier que l'environnement d'exécution et la compilation sont bien en Java25


## Comment lancer le programme

Sur Eclipse, lancer directement le programme sur Main.java se trouvant dans src/fr.uge.azathro

Compilation
```
javac src/fr/uge/azathro/*.java
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



à faire: discard, planète, déroulement de jeu