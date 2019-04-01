package com.poker.hand;

import com.poker.card.CardSuit;
import com.poker.card.PokerCard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PokerHand implements Comparable<PokerHand> {
    private static final int HAND_SIZE = 5;
    private List<PokerCard> cards;

    public PokerHand(final List<PokerCard> cards) {
        if (cards.size() != HAND_SIZE) {
            throw new IllegalArgumentException("A poker hand must contain five cards!");
        }

        this.cards = new ArrayList<PokerCard>();
        this.cards.addAll(cards);
        Collections.sort(this.cards);
    }

    @Override
    public int compareTo(PokerHand other) {
        return this.getHandRanking()
                .compareTo(other.getHandRanking());
    }

    public HandRanking getHandRanking() {
        HandRanking maxRank = HandRanking.SINGLE;
        maxRank = getMaxGroupRanking();

        return maxRank;
    }

    /**
     * Returns the highest-value hand ranking based on card rank groupings (not considering suit
     * or sequence (straights)). The possible values are four-of-a-kind, full house,
     * three-of-a-kind, two-pair, pair, and single.
     *
     * @return the highest-value hand ranking based on card rank groupings
     */
    public HandRanking getMaxGroupRanking() {
        HashMap<PokerCard.Rank, Integer> rankToCountMap = new HashMap();
        int maxCount = 1;

        // we'll use a dictionary lookup to count values
        for (int i = 0; i < cards.size(); i++) {
            PokerCard.Rank currentRank = cards.get(i).getRank();
            Integer currentRankCount = rankToCountMap.get(currentRank);

            if (currentRankCount == null) {
                // first instance of this rank
                rankToCountMap.put(currentRank, 1);
            } else {
                // already exists, increment the value
                currentRankCount++;
                maxCount = currentRankCount > maxCount ? currentRankCount : maxCount;
                rankToCountMap.replace(currentRank, currentRankCount);
            }
        }

        // We can now deduce from the max count and the number of distinct ranks what the max
        // possible grouping is.

        if (maxCount == 4) {
            return HandRanking.FOUR_OF_A_KIND;
        }

        if (maxCount == 3) {
            // this could be a full house or just three of a kind
            Iterator<Map.Entry<PokerCard.Rank, Integer>> it = rankToCountMap.entrySet().iterator();
            if (rankToCountMap.size() == 2) {
                // only two ranks present, meaning this must be a three-of-a-kind and a pair
                return HandRanking.FULL_HOUSE;
            }

            return HandRanking.THREE_OF_A_KIND;
        }

        if (rankToCountMap.size() == 3) {
            // at this point, the only possible way for there to be three distinct ranks is if two
            // pairs are present
            return HandRanking.TWO_PAIR;
        }

        if (rankToCountMap.size() == 4) {
            // same deal, 4 distinct ranks means one pair is present
            return HandRanking.PAIR;
        }

        return HandRanking.SINGLE;
    }

    /**
     * Returns true if the hand contains a straight (of sequential cards); false otherwise.
     *
     * @return true if this hand contains a straight; false otherwise.
     */
    public boolean isStraight() {
        int diff = 0;
        for (int i = 0; i < cards.size() - 1; i++) {
            diff = cards.get(i).difference(cards.get(i + 1));
            if (diff != 1) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns true if all cards in this hand are of a common suit; false otherwise.
     *
     * @return true if all cards in this hand are of a common suit; false otherwise.
     */
    public boolean allOfSameSuit() {
        CardSuit suit = cards.get(0).getSuit();
        for (int i = 1; i < cards.size(); i++) {
            if (cards.get(i).getSuit().compareTo(suit) != 0) {
                return false;
            }
        }

        return true;
    }
}
