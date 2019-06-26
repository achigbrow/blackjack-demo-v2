package edu.cnm.deepdive.blackjackdemo.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import edu.cnm.deepdive.blackjackdemo.model.Card;
import edu.cnm.deepdive.blackjackdemo.model.Deck;
import edu.cnm.deepdive.blackjackdemo.model.Draw;
import edu.cnm.deepdive.blackjackdemo.model.Hand;
import edu.cnm.deepdive.blackjackdemo.service.DeckOfCardsService;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import java.util.List;

public class MainViewModel extends ViewModel {

  private static final int DECKS_IN_SHOE = 6;
  private static final int INITIAL_DRAW = 2;

  private Deck deck;
  private Hand hand;
  private MutableLiveData<List<Card>> cards;

  public LiveData<List<Card>> getCards() {
    if (cards == null) {
      cards = new MutableLiveData<>();
      createDeck();
    }
    return cards;
  }

  public void shuffle() {
    DeckOfCardsService.getInstance().shuffle(deck.getId())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe((d) -> deal()); // FIXME Add to disposable container.
  }

  public void deal() {
    hand = new Hand();
    draw(INITIAL_DRAW);
  }

  public void draw(int numCards) {
    DeckOfCardsService.getInstance().draw(deck.getId(), numCards)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(this::addToHand); // FIXME Add to disposable container.
  }

  private void createDeck() {
    DeckOfCardsService.getInstance().newDeck(DECKS_IN_SHOE)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe((deck) -> {
          this.deck = deck;
          deal();
        }); // FIXME Add to disposable container.
  }

  private void addToHand(Draw draw) {
    for (Card card : draw.getCards()) {
      hand.addCard(card);
    }
    cards.setValue(hand.getCards());
  }

}
