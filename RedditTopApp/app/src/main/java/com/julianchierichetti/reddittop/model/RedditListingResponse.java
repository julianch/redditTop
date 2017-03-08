package com.julianchierichetti.reddittop.model;

import java.util.List;

/**
 * Created by julian on 07-Mar-17.
 */

public class RedditListingResponse {

    public ListingData data;



    public class ListingData {

        public List<FeedChildren> children;
        public String after;

        public class FeedChildren {
            public Feed data;
        }
    }
}
