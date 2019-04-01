package com.poker.hand;

import com.poker.card.PokerCard;
import com.poker.card.PokerCard.CardSuit;

import java.util.ArrayList;
import java.util.List;

public class HandRanking implements Comparable<HandRanking> {
    private HandRankType handRankType;
    private CardSuit suit;

    /**
     * An ordered list of ranks among the groups in a respective hand. This list MUST NOT
     * be re-ordered, as that will ruin any integrity in intra-hand-rank comparisons.
     */
    private final List<PokerCard.Rank> groupRanks;

    public HandRanking(HandRankType handRankType, PokerCard.Rank rank, CardSuit suit) {
        List<PokerCard.Rank> groupRanks = new ArrayList();
        groupRanks.add(rank);

        this.handRankType = handRankType;
        this.groupRanks = groupRanks;
        this.suit = suit;
    }

    public HandRanking(HandRankType handRankType, List<PokerCard.Rank> groupRanks) {
        this(handRankType, groupRanks, null);
    }

    public HandRanking(HandRankType handRankType, List<PokerCard.Rank> groupRanks, CardSuit suit) {
        this.handRankType = handRankType;
        this.groupRanks = groupRanks;
        this.suit = suit;
    }

    public HandRankType getHandRankType() {
        return handRankType;
    }

    public List<PokerCard.Rank> getGroupRanks() {
        return groupRanks;
    }

    @Override
    public int compareTo(HandRanking other) {
        int handRankComparison = this.handRankType.compareTo(other.handRankType);
        if (handRankComparison == 0) {
            // hands have the same ranks -> compare the values of the relevant groups
            for (int i = 0; i < groupRanks.size(); i++) {
                int currentGroupRankComparison =
                        groupRanks.get(i).compareTo(other.groupRanks.get(i));

                if (currentGroupRankComparison != 0) {
                    return currentGroupRankComparison;
                }
            }
        }

        // at this point all other things equal, we'll check the suit to determine tiebreaking
        return suit == null
                ? 0
                : suit.compareTo(other.suit);
    }

    public enum HandRankType {
        ROYAL_FLUSH,
        STRAIGHT_FLUSH,
        FOUR_OF_A_KIND,
        FULL_HOUSE,
        FLUSH,
        STRAIGHT,
        THREE_OF_A_KIND,
        TWO_PAIR,
        PAIR,
        SINGLE;

        public static HandRankType maxOf(HandRankType hr1, HandRankType hr2) {
            if (hr1.compareTo(hr2) < 0) {
                return hr2;
            }

            return hr1;
        }
    }

}