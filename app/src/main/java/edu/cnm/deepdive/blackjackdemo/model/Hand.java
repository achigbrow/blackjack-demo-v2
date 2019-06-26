package edu.cnm.deepdive.blackjackdemo.model;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Hand {

  private static final String TO_STRING_FORMAT = "%d%s";
  private static final String BLACKJACK_STATUS = ": Blackjack!";
  private static final String BUSTED_STATUS = ": Busted!";
  private static final String SOFT_STATUS = " (soft)";
  private static final String OTHER_STATUS = "";

  private final List<Card> cards;
  private int score;
  private boolean soft;
  private boolean busted;
  private boolean blackjack;
  private String status = OTHER_STATUS;

  public Hand() {
    cards = new LinkedList<>();
  }

  public void addCard(Card card) {
    cards.add(card);
    if (!soft
        && (card.getRank() == Rank.ACE)
        && (score <= 21 - Rank.ACE.getSoftValue())) {
      score += Rank.ACE.getSoftValue();
      soft = true;
    } else if (soft
        && score + card.getRank().getHardValue() > 21 ) {
      soft = false;
      score -= Rank.ACE.getSoftValue() - Rank.ACE.getHardValue();
      score += card.getHardValue();
    } else {
      score += card.getHardValue();
    }
    if (score> 21) {
      busted = true;
    } else if (score == 21 && cards.size() == 2){
      blackjack = true;
    }
  }

  public List<Card> getCards() {
    return cards;
  }

  public int getScore() {
    return score;
  }

  public boolean isSoft() {
    return soft;
  }

  public boolean isBusted() {
    return busted;
  }

  private boolean isBlackjack() {
    return blackjack;
  }

  @Override
  public String toString() {
    return String.format(TO_STRING_FORMAT, score, status);
  }

}

