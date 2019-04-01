package com.poker.hand;

import com.poker.card.PokerCard;

import java.util.ArrayList;
import java.util.List;

public class HandRanking implements Comparable<HandRanking> {
    private HandRankType handRankType;

    /**
     * An ordered list of ranks among the groups in a respective hand. This list MUST NOT
     * be re-ordered, as that will ruin any integrity in intra-hand-rank comparisons.
     */
    private final List<PokerCard.Rank> groupRanks;

    public HandRanking(HandRankType handRankType, PokerCard.Rank rank) {
        List<PokerCard.Rank> groupRanks = new ArrayList();
        groupRanks.add(rank);

        this.handRankType = handRankType;
        this.groupRanks = groupRanks;
    }

    public HandRanking(HandRankType handRankType, List<PokerCard.Rank> groupRanks) {
        this.handRankType = handRankType;
        this.groupRanks = groupRanks;
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

        return 0;
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