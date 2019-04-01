package com.poker.hand;

import com.poker.card.CardSuit;
import com.poker.card.PokerCard;
import com.poker.hand.HandRanking.HandRankType;

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

    private PokerCard getHighCard() {
        return this.cards.get(0);
    }

    private PokerCard.Rank getHighestRank() {
        return getHighCard().getRank();
    }

    @Override
    public int compareTo(PokerHand other) {
        HandRanking thisRanking = this.getHandRanking();
        int comparison = thisRanking.compareTo(other.getHandRanking());

        return comparison;
    }

    /**
     * Given two hands of the same ranking, determine which one wins. A positive value indicates
     * this wins; a negative indicates the other hand wings; and zero means they tie.
     *
     * @param other the PokerHand to compare with
     *
     * @return 1 if this wins, -1 if this loses, or 0 if this ties with the other hand
     */
    public int compareSameRankings(PokerHand other, HandRankType ranking) {
        switch (ranking) {
            case STRAIGHT_FLUSH:
            case FLUSH:
            case STRAIGHT:
            case SINGLE:
                // cases where the highest card in the hand will be the tiebreaker
                return this.getHighCard().compareTo(other.getHighCard());
        }

        return 0;
    }

    public HandRanking getHandRanking() {
        HandRankType maxRank = HandRankType.SINGLE;

        boolean isStraight = isStraight();
        boolean isFlush = isFlush();

        if (isFlush) {
            if (isStraight) {
                if (getHighCard().getRank() == PokerCard.Rank.ACE) {
                    return new HandRanking(HandRankType.ROYAL_FLUSH, getHighestRank());
                } else {
                    return new HandRanking(HandRankType.STRAIGHT_FLUSH, getHighestRank());
                }
            } else {
                // is a flush but isn't straight
                maxRank = HandRankType.maxOf(maxRank, HandRankType.FLUSH);
            }
        } else if (isStraight) {
            // not a flush but is a straight
            maxRank = HandRankType.maxOf(maxRank, HandRankType.STRAIGHT);
        }

        maxRank = HandRankType.maxOf(maxRank, getMaxGroupRanking());

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

        if (rankToCountMap.size() == HAND_SIZE) {
            // Short-circuit here if all distinct values: high card will determine the value
            return new HandRanking(HandRankType.SINGLE, getHighestRank());
        }

        // We can now deduce from the max count and the number of distinct ranks what the max
        // possible grouping is.
        PokerCard.Rank quartetRank = null;
        PokerCard.Rank tripletRank = null;
        List<PokerCard.Rank> pairRanks = new ArrayList();
        List<PokerCard.Rank> singleRanks = new ArrayList();

        Iterator<Map.Entry<PokerCard.Rank, Integer>> it = rankToCountMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<PokerCard.Rank, Integer> entry = it.next();
            if (entry.getValue() == 4) {
                quartetRank = entry.getKey();
            } else if (entry.getValue() == 3) {
                tripletRank = entry.getKey();
            } else if (entry.getValue() == 2) {
                pairRanks.add(entry.getKey());
            } else if (entry.getValue() == 1) {
                singleRanks.add(entry.getKey());
            }
        }

        // Now use the rankings we initialized to determine the overall hand ranking.

        if (quartetRank != null) {
            // single rank will have been initialized with the non-quartet rank
            // insert the most important rank, the quartet's rank, at the start of the singles ranks
            singleRanks.add(0, quartetRank);
            return new HandRanking(HandRankType.FOUR_OF_A_KIND, singleRanks);
        }

        // At this point, we'll save the ranks of the singles ("kickers") so they can be used in
        // case of a tie.
        Collections.sort(singleRanks, Collections.reverseOrder());

        if (tripletRank != null) {
            // this could be a full house or just three of a kind
            if (pairRanks.size() > 0) {
                // insert the most important rank, the triplet rank, before the pair rank
                pairRanks.add(0, tripletRank);
                return new HandRanking(HandRankType.FULL_HOUSE, pairRanks);
            }

            // insert the most important rank, the triplet rank, at the start of the kickers list
            singleRanks.add(0, tripletRank);
            return new HandRanking(HandRankType.THREE_OF_A_KIND, singleRanks);
        }

        if (pairRanks.size() == 2) {
            // make sure we take the higher rank first
            Collections.sort(pairRanks);
            // put the last card (the "kicker") at the lowest priority rank
            pairRanks.add(2, singleRanks.get(0));
            return new HandRanking(HandRankType.TWO_PAIR, pairRanks);
        }

        if (pairRanks.size() == 1) {
            // put the most important rank, the pair's, at the start of the ranks list
            singleRanks.add(0, pairRanks.get(0));
            return new HandRanking(HandRankType.PAIR, singleRanks);
        }

        return new HandRanking(HandRankType.SINGLE, singleRanks);
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
    public boolean isFlush() {
        CardSuit suit = cards.get(0).getSuit();
        for (int i = 1; i < cards.size(); i++) {
            if (cards.get(i).getSuit().compareTo(suit) != 0) {
                return false;
            }
        }

        return true;
    }
}
