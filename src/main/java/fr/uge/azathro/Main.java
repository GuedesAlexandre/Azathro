package main.java.fr.uge.azathro;

import main.java.fr.uge.azathro.domain.Card;
import main.java.fr.uge.azathro.domain.Hand;
import main.java.fr.uge.azathro.domain.types.rank.King;
import main.java.fr.uge.azathro.domain.types.suit.Club;

import java.util.List;

public class Main {
    static void main() {
        var card = new Card(new Club(), new King());
        var card2 = new Card(new Club(), new King());
        var card3 = new Card(new Club(), new King());
        var card4 = new Card(new Club(), new King());
        var card5 = new Card(new Club(), new King());
        var card6 = new Card(new Club(), new King());
        var card7 = new Card(new Club(), new King());
        var card8 = new Card(new Club(), new King());
        var hand = new Hand(List.of(card, card2, card3, card4, card5, card6, card7, card8));
        IO.println(hand);
        IO.println(card);
    }
}
