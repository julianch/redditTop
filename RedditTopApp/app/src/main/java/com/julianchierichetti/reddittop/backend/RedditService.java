package com.julianchierichetti.reddittop.backend;

import com.julianchierichetti.reddittop.model.RedditListingResponse;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by julian on 07-Mar-17.
 */

public interface RedditService {
    @GET("top/.json?limit=10")
    Observable<RedditListingResponse> getTops(@Query("after") String after);
}
