package com.julianchierichetti.reddittop.backend;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.julianchierichetti.reddittop.model.RedditListingResponse;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

/**
 * Created by julian on 07-Mar-17.
 */

public class RedditClient {

    private static final String BACKEND_BASE_URL = "https://www.reddit.com/";

    private static RedditClient instance;
    private RedditService redditService;

    private RedditClient() {
        final Gson gson =
                new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(BACKEND_BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        redditService = retrofit.create(RedditService.class);
    }

    public static RedditClient getInstance() {
        if (instance == null) {
            instance = new RedditClient();
        }
        return instance;
    }

    public Observable<RedditListingResponse> getTops(String after) {
        return redditService.getTops(after);
    }
}
