package com.julianchierichetti.reddittop.model;

import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by julian on 07-Mar-17.
 */

public class Feed extends RealmObject {

    public String author;
    public String title;
    public long created_utc;
    public String thumbnail;
    public long num_comments;
    public String sourcePictureUrl;
    @Ignore
    public Preview preview;
    @PrimaryKey
    public String id;

    public Feed(){}

    public class Preview {
        public List<Image> images;
        public class Image {
            public Picture source;
        }
    }
}
