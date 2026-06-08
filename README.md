<p style="text-align: center;">
  <img src="assets/logo.png" alt="Logo" />
</p>

## Présentation du projet

Azathro est une version lite du jeu Balatro dont le style graphique s'inspire de l'univers Lovecraft.


## Structure du projet

Le projet est codé en java25 et suit un modèle MVC (Model View Controller), structuré comme suit:

```
src/fr/uge/azathro/
├── Main.java
└── domain/
    ├── types/               
    │   ├── combination/
    │   ├── rank/
    │   ├── suit/
    │   └── planet/         
    ├── Card.java      
    ├── Blind.java       
    ├── Deck.java      
    ├── Hand.java        
    └── Dealer.java
└── model/
    ├── GameState.java 
    └── PlayerState.java
└── controller/
    ├── GameController.java
    └── engine/
        └── GameEngine.java
└── view/   
    ├── assets/
    ├── AssetManager.java
    ├── ConsoleView.java
    ├── GraphicView.java 
    └── View.java       
```
On a fait le choix dans le domain d'inclure tous les objets représentants des notions "métiers" du jeu balatro, comme les cartes, les blinds, les mains, les planètes etc... 
On a aussi inclus dans le domain un Dealer qui est chargé de calculer le score d'une main (comme le ferait le croupier dans un casino).
Le model contient l'état du jeu et du joueur, tandis que le controller gère la logique du jeu et la communication entre le model et la view. 
La view est chargée de l'affichage du jeu.
## Comment importer le projet

- Télécharger et décompresser l'archive .zip
- Ouvrir le projet dans Eclipse (open projects from file system).
- Vérifier que l'environnement d'exécution et la compilation sont bien en Java25


## Comment lancer le programme

- Sur Eclipse, lancez directement le programme sur Main.java (se trouvant dans src/fr.uge.azathro)
- Si vous utilisez la ligne de commande, compilez tous les fichiers .java et lancez Main.java.
- Si vous souhaitez lancer la vue graphique, allez dans dans "Run Configurations/ Arguments / Program arguments" et ajoutez l'argument "--graphic" à l'exécution de Main.java.

## Fonctionalités implémentées
- représentation des cartes avec les combinaisons de poker
- une pioche de 52 cartes
- calcul correct du score des mains. Score cumulé à chaque main et réinitialisé à 0 à chaque début de blind
- boucle de jeu: 5 blinds constituées d'un nombre de mains limité à 4, le jouer pioche 8 cartes et joue une main de 1 à 5 cartes à chaque tour. L'échec d'un blind met immédiatement fin au jeu.
- Gestion des planètes en fin de Blind avec leurs effets sur les combinaisons
- vue console fonctionnelle
- vue graphique fonctionnelle
- score par cartes
- défausse active

## Comment jouer le jeu
Le joueur pioche des cartes, sélectionne une main de 1 à 5 cartes pour former une combinaison de poker, accumule des points et tente de franchir une série de 5 *blinds* (seuils de score à atteindre) où la difficulté accroît au cours du jeu.
Entre chaque blind, le joueur obtient une carte Planète (bonus) qui améliore définitivement une combinaison pour la suite de la partie.

Au cours du jeu, le joueur peut défausser manuellement des cartes (dans le but de chercher une meilleure combinaison) en tapant sur la touche `D`. La touche `Space` permet de jouer une main dans l'interface graphique (touche `J` si vue console).


## Avec cette architecture, combien coûteront les autres extras ?

#### Analyse par extension

- C. Jokers — Effort FAIBLE

Le pattern existe déjà avec Planet. Il suffit de créer une interface scellée JokerEffect, d'ajouter `List<Joker>` dans PlayerState, et d'étendre Dealer.evaluate() pour appliquer les effets jokers après les planets.

- D. Monnaie & Boutique — Effort MOYEN

Ajouter int coins dans PlayerState, créer un Shop record avec des ShopItem, et insérer une phase boutique dans GameEngine.advanceToNextBlind(). Le View sealed oblige à implémenter showShop() dans ConsoleView ET GraphicView, ce qui peut être plus long en terme de design.

- E. Blinds avec contraintes — Effort FAIBLE

Blind est un record. On ajouterait Optional<BlindConstraint> où BlindConstraint est une sealed interface (DisabledCombinations, HiddenHand, etc.). Dealer.evaluate() vérifie la contrainte avant de retourner la combinaison. Chaque nouveau type de contrainte = un nouveau record.

- F. Deck personnalisable — Effort FAIBLE

Deck encapsule déjà le ArrayList<Card>. On ajouterait juste addCard() / removeCard(), et on insère une phase dans GameEngine.advanceToNextBlind() (même point d'ancrage que D).

- G. Sauvegarde — Effort MOYEN

PlayerState est un record → sérialisation triviale. On représente un SaveManager dans un package persistence/. Les records sérialisent naturellement leurs composants. Main.java charge le save s'il existe au démarrage.

- H. Mode infini & High Score — Effort TRÈS FAIBLE

On supprime la constante NB_BLINDS = 5 dans GameState (ou la rendre conditionnelle via --infinite). La progression ×1.5 est déjà infinie par nature. Il nous suffit alors d'ajouter une interface fonctionnelle HighScoreManager qui remplie un fichier.


