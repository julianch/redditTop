package com.julianchierichetti.reddittop;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by julian on 07-Mar-17.
 */

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(config);
    }
}
