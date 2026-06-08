# Rapport projet Azathro

L'objectif de ce rapport est d’expliquer les principaux choix de conception réalisés pendant le développement du projet, et d’établir un retour d’expérience sur notre travail.

# Partie 1 — Développement du projet


## 1. Architecture générale

Notre projet suit une architecture **MVC** stricte, découpée en quatre packages principaux :

```
fr.uge.azathro
├── domain/          ← règles métier (cartes, combinaisons, évaluation, pioche/défausse, blind)
├── model/           ← état de la partie (GameState, PlayerState)
├── controller/      ← coordination (GameController, GameEngine)
└── view/            ← affichage (ConsoleView, GraphicView)
```

Cette séparation nous permet notamment de supporter deux modes d'affichage sans toucher à la logique de jeu : un mode **console** (pour les tests et la CI) et un mode **graphique** basé sur la bibliothèque `zen`.

Le mode est choisi au lancement via un argument CLI, c'est-à-dire en ajoutant `--graphic` dans Run Configurations/ Program arguments 

```java
boolean graphic = Arrays.asList(args).contains("--graphic");
View view = graphic ? new GraphicView() : new ConsoleView();
```

---

## 2. Choix de conception

### 2.1 `View` : interface scellée

L'interface `View` est déclarée `sealed`, ce qui garantit statiquement que les deux seuls implémenteurs possibles sont `ConsoleView` et `GraphicView` :

```java
public sealed interface View permits ConsoleView, GraphicView {
    void showIntro();
    void showGameState(GameState gameState, Set<Integer> selectedIndexes,
                       String lastCombination, Integer lastScore);
    void showBlindSuccess();
    void showGameOver();
    void showPlanetDrawn(Planet planet);
    void showGameEnd();
}
```

Le `GameController` peut alors utiliser un pattern matching exhaustif sur le type de vue pour adapter la boucle d'événements et envoyer vers la bonne view l'état de la partie en cours.

```java
switch (view) {
    case GraphicView gv -> Application.run(Color.DARK_GRAY, ctx -> graphicLoop(ctx, gv));
    case ConsoleView cv -> consoleLoop(cv);
}
```

### 2.2 `Combination` : interface scellée et pattern matching

Les combinaisons de poker sont modélisées via une interface scellée `Combination` dont chaque implémentation est un `record` immuable portant ses deux paramètres de score (`chips`, `multiplier`) :

Pourquoi le choix d'un record ? On a fait ce choix car chaque combinaison existe à un instant t dans un état différent donc on construit des record par niveau.

```java
public sealed interface Combination
    permits Flush, FourOfAKind, FullHouse, HighCard,
            Pair, Straight, StraightFlush, ThreeOfAKind, TwoPair { ... }

public record Pair(int chips, int multiplier) implements Combination { ... }
```

Le calcul du score s'appuie sur un switch exhaustif, garantissant qu'aucune combinaison ne peut être oubliée à la compilation :

```java
static int computeScore(Combination combination) {
    return switch (combination) {
        case HighCard h       -> h.chips() * h.multiplier();
        case Pair p           -> p.chips() * p.multiplier();
        case TwoPair tp       -> tp.chips() * tp.multiplier();
        // ...
        case StraightFlush sf -> sf.chips() * sf.multiplier();
    };
}
```

### 2.3 Immutabilité de l'état joueur

`PlayerState` est un `record` Java. Modifier l'état du joueur (jouer une main, se défausser) produit une **nouvelle instance** plutôt que de muter l'objet existant, ce qui évite les effets de bord :

```java
public PlayerState withScoreAndDecrementHand(int additionalScore) {
    return new PlayerState(name, totalScore + additionalScore,
                           handCount - 1, discardCount, planetDrawed);
}
```

### 2.4 `Dealer` : interface utilitaire à méthodes statiques

L'évaluation d'une main est entièrement encapsulée dans l'interface `Dealer` via une méthode statique, notre dealer c'est notre outil à évaluation.
Ce choix évite d'instancier un objet sans état et maintient la logique d'évaluation en un seul endroit :

```java
public interface Dealer {
    static Combination evaluate(Hand hand, Map<Planet, Integer> planets) { ... }
}
```

L'algorithme repose sur le comptage des occurrences de valeurs (`countByValue`) et une détection de suite (`isStraight`) qui gère le cas particulier de la *wheel* (A-2-3-4-5) :

```java
var isWheel = sorted.equals(List.of(2, 3, 4, 5, 14));
return isNormalStraight || isWheel;
```

---

## 3. Système de score et Planètes

Le score d'une combinaison suit la formule : **Score = Chips × Multiplicateur**.

Les *chips* de base correspondent à la somme des valeurs des cartes jouées (via notre Extra), auxquelles s'ajoutent des chips fixes selon la combinaison (ex. +60 pour un carré). Le multiplicateur est fixe par combinaison (ex. ×7 pour un carré).

Le système de **Planètes**  augmente de façon permanente les paramètres d'une combinaison spécifique. Chaque planète est associée à une combinaison, un bonus de chips et un bonus de multiplicateur :

```java
public enum Planet {
    MERCURE("Pair",           15, 1),
    MARS("Four of a kind",    30, 3),
    NEPTUNE("Straight Flush", 40, 4),
    // ...
}
```

À chaque blind franchi, le joueur tire une planète aléatoire. Les planètes accumulées sont stockées dans une `Map<Planet, Integer>` dans `PlayerState`. Au moment de l'évaluation, les bonus sont appliqués en multipliant le niveau de la planète par ses bonus unitaires :

```java
private static int bonusChips(Planet planet, Map<Planet, Integer> planets) {
    return planet.bonusChips() * planets.getOrDefault(planet, 0);
}
```

---

## 4. Gestion du paquet de cartes

Le `Deck` maintient deux listes : le paquet principal et une **pile de défausse**. Lorsque le paquet ne contient plus assez de cartes pour une pioche, il se recharge automatiquement depuis la défausse avant d'être mélangé à nouveau:

```java
public void refillDeck(int cardsToDraw) {
    if (deck.size() >= cardsToDraw) return;
    deck.addAll(discardPile);
    discardPile.clear();
    Collections.shuffle(deck);
}
```

Les cartes jouées et défaussées rejoignent la pile de défausse via `addToDiscard`. Ce mécanisme garantit que le joueur ne manque jamais de cartes tout en conservant une certaine imprévisibilité.

---

## 5. Vue graphique : positionnement proportionnel

Tous les éléments de `GraphicView` sont positionnés en **coordonnées relatives** à la taille de la fenêtre, via des constantes de ratio :

```java
private static final double CARD_WIDTH_RATIO    = 0.09d;
private static final double BOTTOM_MARGIN_RATIO = 0.05d;
private static final double GAP_RATIO           = 0.012d;
```

Cela rend l'interface redimensionnable sans recalcul manuel. La détection du clic sur une carte repose sur le même système : on recalcule à la volée l'index de la carte à partir de la position X du pointeur et des dimensions courantes de la fenêtre.


---

## 6. Contrôle du jeu

Nous avons fait le choix de séparer le contrôleur en deux parties : 
- Le `GameEngine` qui contient les opérations métiers principales comme remplir une main, jouer une main, défausser manuellement des cartes, vérifier la fin d'un blind/d'une partie, etc...
- Le `GameController` qui s'occupe uniquement de la boucle d'événements et de l'interaction avec l'utilisateur en appelant les méthodes du moteur.

Nous avons choisi cette séparation pour garder la logique de jeu indépendante de l’interface. Cela rend également le code plus lisible et plus facile à faire évoluer.

---

## 7. Chargement de ressources

On a séparé le chargement des ressources graphiques (les images et le font) dans `AssetManager` afin d'éviter d'alourdir `GraphicView` et d'assurer que GraphicView se charge uniquement de l'affichage graphique. 
Les ressources chargées sont alors des constantes définies dans la classe, et peuvent être appelées dans GraphicView.

---

# Partie 2 — Retour d'expérience

## 1. Organisation du travail et répartition des tâches
Nous avons travaillé avec Git, en nous répartissant les tâches en plusieurs branches feature/. 

| Personne  | Tâches principales |
| --- | --- |
| Alexandre | model, domain, GameEngine et première version de GraphicView |
| Julie | Logique de jeu GameController, ConsoleView, amélioration de GraphicView |

L'avancement du travail et les échanges étaient réguliers avec environ une pull request/semaine. Chaque modification était relue par l’autre membre du binôme avant d’être intégrée, afin de se mettre d'accord sur les choix de conception et de s’assurer que les interfaces entre les modules restaient cohérentes avant d'ajouter de nouvelles fonctionnalités.

La répartition des tâches a été claire et assez équilibrée. Chacun a pu contribuer sur une partie bien définie du projet, tout en gardant une vue d’ensemble sur le fonctionnement global. 


## 2. Difficultés rencontrées

**Dissociation de la logique et de l'affichage.** La tentation initiale était de mélanger état et rendu. Le refactoring vers une architecture MVC propre, avec un `GameEngine` dédié qui ne connaît pas la vue, a demandé plusieurs itérations (branches `feature/codebase-architecture`, `feature/refacto-beta`).

**Gestion de la défausse active.** La première version ne permettait de défausser qu'en fin de tour. L'implémentation de la *discard active* (défausser des cartes sélectionnées sans jouer la main) a nécessité d'introduire `discardActiveCards` dans `GameEngine` et d'ajuster le flux d'événements du contrôleur pour distinguer l'action « jouer » (ESPACE) de l'action « défausser » (D).

**Intégration des sprites de cartes.** Le chargement des assets graphiques pouvant échouer (chemin introuvable, format non supporté), un `AssetManager` avec mécanisme de *fallback* a été introduit : si l'image d'une carte est absente, la vue affiche un rectangle avec le nom textuel de la carte.


**Prise en main de la bibliothèque zen.** 
La bibliothèque zen nous a nécessité un temps d'adaptation, notamment pour la gestion des événements et du rendu graphique, car il y a peu de documentations et d'exemples d'usage de la bibliothèque sur Internet.

---

## 3. Bilan du projet

Nous aurions aimé avoir davantage de temps pour peaufiner le projet, notamment pour intégrer d’autres extras, enrichir l’interface et améliorer l’expérience utilisateur. Certains extras auraient certainement pu être ajoutées avec plus de temps.
Malgré cela, le projet a été globalement une très bonne expérience pour nous. Il nous a permis de consolider les notions vues en cours et de les appliquer sur des projets plus conséquents comme ce celui-ci, tout en adoptant une architecture moderne de MVC. Travailler sur un projet complet, depuis le modèle jusqu’à la vue graphique, a été particulièrement enrichissant pour nous, autant sur le plan technique que sur le plan de l’organisation du travail en équipe.