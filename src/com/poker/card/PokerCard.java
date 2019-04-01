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
        ACE(14),
        KING(13),
        QUEEN(12),
        JACK(11),
        TEN(10),
        NINE(9),
        EIGHT(8),
        SEVEN(7),
        SIX(6),
        FIVE(5),
        FOUR(4),
        THREE(3),
        TWO(2);

        private final int value;

        Rank(final int value) {
            this.value = value;
        }
    }
}
