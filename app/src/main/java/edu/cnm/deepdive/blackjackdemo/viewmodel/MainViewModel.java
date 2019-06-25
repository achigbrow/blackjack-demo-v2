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
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.List;

public class MainViewModel extends ViewModel {

  private static final int DECKS_IN_SHOE = 6;
  private static final int INITIAL_DRAW = 2;

  private Deck deck;
  private Hand hand;
  private MutableLiveData<List<Card>> cards = new MutableLiveData<>();
  private CompositeDisposable pendingDisposal = new CompositeDisposable();

  public MainViewModel() {
    createDeck();
  }

  public LiveData<List<Card>> getCards() {
    return cards;
  }

  private void createDeck() {
    Disposable disp = DeckOfCardsService.getInstance().newDeck(DECKS_IN_SHOE)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(this::initDeck);
    pendingDisposal.add(disp);
  }

  public void shuffle() {
    Disposable disp = DeckOfCardsService.getInstance().shuffle(deck.getId())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe((d) -> dealHand());
    pendingDisposal.add(disp);
  }

  public void draw(int numCards) {
    Disposable disp = DeckOfCardsService.getInstance().draw(deck.getId(), numCards)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(this::addToHand);
    pendingDisposal.add(disp);
  }

  private void initDeck(Deck deck) {
    this.deck = deck;
    dealHand();
  }

  public void dealHand() {
    hand = new Hand();
    draw(2);
  }

  private void addToHand(Draw draw) {
    for (Card card : draw.getCards()) {
      hand.addCard(card);
    }
    cards.setValue(hand.getCards());
  }

  public void disposeAllPending() {
    pendingDisposal.clear();
  }

}
