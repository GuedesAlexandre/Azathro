<p style="text-align: center;">
  <img src="assets/logo.png" alt="Logo" />
</p>

Ce projet est une version lite du jeu Balatro.
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
|   │   ├── suit/
|   │   └── planet/         
│   ├── Card.java      
│   ├── Blind.java       
│   ├── Deck.java      
│   ├── Hand.java        
│   └── Dealer.java
├── model/
│   ├── GameState.java 
│   └── PlayerState.java
├── controller/
│   ├── engine/
    │   └── GameEngine.java
    ├── GameController.java
└── view/   
    ├── assets/
    ├── ConsoleView.java
    ├── GraphicView.java 
    └── View.java
              
```
On a fait le choix dans le domain d'inclure tous les objets représentants des notions "métiers" du jeu balatro, comme les cartes, les blinds, les mains, les planètes etc... 
On a aussi inclus dans le domain un Dealer qui est chargé de calculer le score d'une main. (Comme le ferait le croupier dans un casino).
Le model contient l'état du jeu et du joueur, tandis que le controller gère la logique du jeu et la communication entre le model et la view. 
La view est chargée de l'affichage du jeu.
## Comment importer le projet

- Télécharger et décompresser l'archive .zip
- Ouvrir le projet dans Eclipse (open projects from file system).
- Vérifier que l'environnement d'exécution et la compilation sont bien en Java25


## Comment lancer le programme

- Sur Eclipse, lancer directement le programme sur Main.java (se trouvant dans src/fr.uge.azathro)
Si vous utilisez la ligne de commande, compilez tous les fichiers .java et lancez Main.java.
Si vous souhaitez lancer la vue graphique, rajoutez l'argument "--graphic" à l'exécution de Main.java

## Fonctionalités implémentées
- représentation des cartes avec les combinaisons de poker
- une pioche de 52 cartes
- calcul correct du score des mains. Score cumulé à chaque main et réinitialisé à 0 à chaque début de blind
- boucle de jeu: 5 blinds constituées d'un nombre de mains limités, le jouer pioche 8 cartes et joue une main de 1 à 5 cartes à chaque tour. L'échec d'un blind met immédiatement fin au jeu.
- Gestion des planètes en fin de Blind avec leurs effets sur les combinaisons
- vue console fonctionnelle
- vue graphique fonctionnelle
- score par cartes
- défausse active


## Avec notre archi combien ça coûte finalement les autres extras ?
Analyse par extension

C. Jokers — Effort FAIBLE

Le pattern existe déjà avec Planet. Il suffit pour nous créer une sealed interface JokerEffect, ajouter List<Joker> dans PlayerState, et étendre Dealer.evaluate() pour appliquer les effets jokers après les planets.

D. Monnaie & Boutique — Effort MOYEN

Ajouter int coins dans PlayerState, créer un Shop record avec des ShopItem, et insérer une phase boutique dans GameEngine.advanceToNextBlind(). Le View sealed oblige à implémenter showShop() dans ConsoleView ET GraphicView — à faire ensemble. (c'est peut être le plus long question de design)

E. Blinds avec contraintes — Effort FAIBLE

Blind est un record. On ajouterait Optional<BlindConstraint> où BlindConstraint est une sealed interface (DisabledCombinations, HiddenHand, etc.). Dealer.evaluate() vérifie la contrainte avant de retourner la combinaison. Chaque nouveau type de contrainte = un nouveau record.

F. Deck personnalisable — Effort FAIBLE

Deck encapsule déjà le ArrayList<Card>. On ajouterait juste addCard() / removeCard(), et on insère une phase dans GameEngine.advanceToNextBlind() (même point d'ancrage que D).

G. Sauvegarde — Effort MOYEN

PlayerState est un record → sérialisation triviale. On représente un SaveManager dans un package persistence/. Les records sérialisent naturellement leurs composants. Main.java charge le save s'il existe au démarrage.

H. Mode infini & High Score — Effort TRÈS FAIBLE

On supprime la constante NB_BLINDS = 5 dans GameState (ou la rendre conditionnelle via --infinite). La progression ×1.5 est déjà infinie par nature.On ajoute une interface fonctionnelle HighScoreManager qui remplie un fichier, c'est tout.


