package com.poker.card;

public class PokerCard implements Comparable<PokerCard> {
    Rank rank;
    CardSuit suit;

    public PokerCard(Rank rank, CardSuit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public CardSuit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    public int difference(PokerCard otherCard) {
        return this.rank.value - otherCard.rank.value;
    }

    @Override
    public int compareTo(PokerCard other) {
        if (this.rank.compareTo(other.rank) == 0) {
            // if ranks are equal, the tie is broken based on suit priority
            return this.suit.compareTo(other.suit);
        }

        // else return based on which card has the higher rank
        return this.rank.compareTo(other.rank);
    }

    public enum Rank {
        TWO(2),
        THREE(3),
        FOUR(4),
        FIVE(5),
        SIX(6),
        SEVEN(7),
        EIGHT(8),
        NINE(9),
        TEN(10),
        JACK(11),
        QUEEN(12),
        KING(13),
        ACE(14);

        private final int value;

        Rank(final int value) {
            this.value = value;
        }
    }
}
