package com.poker.hand;

import com.poker.card.CardSuit;
import com.poker.card.PokerCard;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class PokerHandTest {
    private final List<PokerCard> royalFlushHeartsCards = createPokerHandCards(
            new PokerCard(PokerCard.Rank.JACK, CardSuit.HEARTS),
            new PokerCard(PokerCard.Rank.QUEEN, CardSuit.HEARTS),
            new PokerCard(PokerCard.Rank.ACE, CardSuit.HEARTS),
            new PokerCard(PokerCard.Rank.TEN, CardSuit.HEARTS),
            new PokerCard(PokerCard.Rank.KING, CardSuit.HEARTS)
    );

    private final List<PokerCard> royalFlushSpadesCards = createPokerHandCards(
            new PokerCard(PokerCard.Rank.ACE, CardSuit.SPADES),
            new PokerCard(PokerCard.Rank.KING, CardSuit.SPADES),
            new PokerCard(PokerCard.Rank.QUEEN, CardSuit.SPADES),
            new PokerCard(PokerCard.Rank.JACK, CardSuit.SPADES),
            new PokerCard(PokerCard.Rank.TEN, CardSuit.SPADES)
    );

    private final List<PokerCard> lowStraightCards = createPokerHandCards(
            new PokerCard(PokerCard.Rank.TWO, CardSuit.SPADES),
            new PokerCard(PokerCard.Rank.THREE, CardSuit.SPADES),
            new PokerCard(PokerCard.Rank.FIVE, CardSuit.HEARTS),
            new PokerCard(PokerCard.Rank.FOUR, CardSuit.DIAMONDS),
            new PokerCard(PokerCard.Rank.SIX, CardSuit.CLUBS)
    );

    private final List<PokerCard> highStraightCards = createPokerHandCards(
            new PokerCard(PokerCard.Rank.NINE, CardSuit.HEARTS),
            new PokerCard(PokerCard.Rank.KING, CardSuit.DIAMONDS),
            new PokerCard(PokerCard.Rank.QUEEN, CardSuit.SPADES),
            new PokerCard(PokerCard.Rank.JACK, CardSuit.CLUBS),
            new PokerCard(PokerCard.Rank.TEN, CardSuit.SPADES)
    );

    private final List<PokerCard> lowFlushCards = createPokerHandCards(
            new PokerCard(PokerCard.Rank.TWO, CardSuit.DIAMONDS),
            new PokerCard(PokerCard.Rank.SEVEN, CardSuit.DIAMONDS),
            new PokerCard(PokerCard.Rank.FIVE, CardSuit.DIAMONDS),
            new PokerCard(PokerCard.Rank.FOUR, CardSuit.DIAMONDS),
            new PokerCard(PokerCard.Rank.SIX, CardSuit.DIAMONDS)
    );

    @Test
    public void pokerHand_notEnoughCards_exceptionThrown() {
        List<PokerCard> cards = new ArrayList();
        cards.add(new PokerCard(PokerCard.Rank.JACK, CardSuit.HEARTS));

        try {
            PokerHand hand = new PokerHand(cards);
        } catch (IllegalArgumentException ex) {
            return;
        }

        fail("Single-card hands aren't allowed");
    }

    @Test
    public void pokerHand_tooManyCards_exceptionThrown() {
        List<PokerCard> cards = new ArrayList();
        cards.add(new PokerCard(PokerCard.Rank.JACK, CardSuit.HEARTS));
        cards.addAll(lowFlushCards);

        try {
            PokerHand hand = new PokerHand(cards);
        } catch (IllegalArgumentException ex) {
            return;
        }

        fail("Six-card hands aren't allowed");
    }

    @Test
    public void handRanking_royalFlush_happyCase() {
        PokerHand royalFlushHearts = new PokerHand(royalFlushHeartsCards);
        assertEquals(HandRanking.ROYAL_FLUSH, royalFlushHearts.getHandRanking());
    }

    @Test
    public void allOfSameSuit_notAllCardsInSameSuit_returnsFalse() {
        PokerHand lowStraight = new PokerHand(lowStraightCards);
        assertFalse(lowStraight.allOfSameSuit());
    }

    @Test
    public void allOfSameSuit_allCardsInSameSuit_returnsTrue() {
        PokerHand flush = new PokerHand(lowFlushCards);
        assertTrue(flush.allOfSameSuit());
    }

    @Test
    public void isStraight_cardsNotInSequence_returnsFalse() {
        PokerHand flush = new PokerHand(lowFlushCards);
        assertFalse(flush.isStraight());
    }

    @Test
    public void isStraight_cardsInSequence_returnsTrue() {
        PokerHand flush = new PokerHand(royalFlushHeartsCards);
        assertTrue(flush.isStraight());
    }

    @Test
    public void royalFlushTie_betterSuitWins() {
        PokerHand royalFlushSpades = new PokerHand(royalFlushSpadesCards);
        PokerHand royalFlushHearts = new PokerHand(royalFlushHeartsCards);

        assertEquals(1, royalFlushSpades.compareTo(royalFlushHearts),
                "Royal flush spades should beat royal flush hearts");
    }

    @Test
    public void getMaxGroupRanking_handIsStraight_returnsSingle() {
        PokerHand royalFlush = new PokerHand(royalFlushHeartsCards);
        assertEquals(HandRanking.SINGLE, royalFlush.getMaxGroupRanking());
    }

    // --------------
    // Helper Methods

    private static List<PokerCard> createPokerHandCards(PokerCard pc1, PokerCard pc2,
            PokerCard pc3, PokerCard pc4, PokerCard pc5) {
        ArrayList<PokerCard> retVal = new ArrayList();
        retVal.add(pc1);
        retVal.add(pc2);
        retVal.add(pc3);
        retVal.add(pc4);
        retVal.add(pc5);

        return retVal;
    }
}
