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

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import edu.cnm.deepdive.blackjackdemo.model.Card;
import edu.cnm.deepdive.blackjackdemo.model.Deck;
import edu.cnm.deepdive.blackjackdemo.model.Draw;
import edu.cnm.deepdive.blackjackdemo.model.Hand;
import edu.cnm.deepdive.blackjackdemo.service.DeckOfCardsService;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import java.util.List;

public class MainViewModel extends ViewModel {

  private static final int DECKS_IN_SHOE = 6;
  private static final int INITIAL_DRAW = 2;

  private MutableLiveData<Deck> deck = new MutableLiveData<>();
  private MutableLiveData<Hand> hand = new MutableLiveData<>();
  private MutableLiveData<List<Card>> cards = new MutableLiveData<>();

  {
    createDeck();
  }

  public MutableLiveData<Deck> getDeck() {
    return deck;
  }

  public MutableLiveData<Hand> getHand() {
    return hand;
  }

  public MutableLiveData<List<Card>> getCards() {
    return cards;
  }

  public void shuffle() {
    DeckOfCardsService.getInstance().shuffle(deck.getValue().getId())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe((d) -> deal()); // FIXME Add to disposable container.

  }

  public void deal() {
    hand.setValue(new Hand());
    draw(INITIAL_DRAW);
  }

  public void draw(int numCards) {
    DeckOfCardsService.getInstance().draw(deck.getValue().getId(), numCards) // gets the deck object from the thingies.
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(this::addToHand);
  }

  private void createDeck() {
    DeckOfCardsService.getInstance().newDeck(DECKS_IN_SHOE)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe((deck) -> {
          this.deck.setValue(deck);
          deal();
        }); // FIXME Add to disposable container.
  }


  private void addToHand(Draw draw) { //Draw is what we get from the developer defined data service when we ask it to return the draw from a particular deck id
    Hand hand = this.hand.getValue(); //gethand returns live data, getvalue returns the i don't know ( I do, but not for here, no idea what kind of thang this dude be talking about
    for (Card card: draw.getCards()) { //iterates over the cards, gets them and adds them to hand, and updates Hand hand
      hand.addCard(card); // just adds to a list, does not add to our live data, our observed data
    }
    cards.setValue(hand.getCards()); //take the list that is currently in this hand and post it to our cards mutable data.

    //in fact hand.getCards is going to return the same list object but with additional items in it.
  }

}