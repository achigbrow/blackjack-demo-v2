package edu.cnm.deepdive.blackjackdemo.service;

import edu.cnm.deepdive.blackjackdemo.BuildConfig;
import edu.cnm.deepdive.blackjackdemo.model.Deck;
import edu.cnm.deepdive.blackjackdemo.model.Draw;
import io.reactivex.Single;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DeckOfCardsService {

  @GET("deck/new/shuffle/")
  Single<Deck> newDeck(@Query("deck_count") int count);

  @GET("deck/{deckId}/draw/")
  Single<Draw> draw(@Path("deckId") String deckId, @Query("count") int count);

  @GET("deck/{deckId}/shuffle/")
  Single<Deck> shuffle(@Path("deckId") String deckId);

  static DeckOfCardsService getInstance() {
    return InstanceHolder.INSTANCE;
  }

  class InstanceHolder {

    private static final DeckOfCardsService INSTANCE;

    static {
      HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
      interceptor.setLevel(Level.BODY);
      OkHttpClient client = new OkHttpClient.Builder()
          .addInterceptor(interceptor)
          .build();
      Retrofit retrofit = new Retrofit.Builder()
          .baseUrl(BuildConfig.BASE_URL)
          .client(client)
          .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
          .addConverterFactory(GsonConverterFactory.create())
          .build();
      INSTANCE = retrofit.create(DeckOfCardsService.class);
    }

  }

}
