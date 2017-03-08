package com.julianchierichetti.reddittop.ui;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.julianchierichetti.reddittop.R;
import com.julianchierichetti.reddittop.backend.RedditClient;
import com.julianchierichetti.reddittop.model.Feed;
import com.julianchierichetti.reddittop.model.RedditListingResponse;
import com.julianchierichetti.reddittop.ui.adapter.FeedAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class TopRedditsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {


    @BindView(R.id.top_reddits_listview)
    ListView mListView;
    @BindView(R.id.top_reddits_swipe_layout)
    SwipeRefreshLayout mSwipeLayout;
    @BindView(R.id.top_reddits_empty_view)
    TextView mEmptyView;

    private Subscription mSubscription;
    private Realm mRealm;
    private FeedAdapter mAdapter;
    private String mAfterKey;
    private boolean mIsLoadingMore = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_reddits);
        ButterKnife.bind(this);
        mRealm = Realm.getDefaultInstance();
        initListView();
        initData();
    }




    public void initListView() {
        RealmResults<Feed> topFeeds = mRealm.where(Feed.class).findAllAsync();
        mAdapter = new FeedAdapter(this, topFeeds);
        mListView.setAdapter(mAdapter);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0) {
                    if (!mIsLoadingMore && mAfterKey != null) {
                        mIsLoadingMore = true;
                        loadMoreItems();
                    }
                }
            }
        });
        mListView.setEmptyView(mEmptyView);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }

    public void initData() {
        mIsLoadingMore = true;
        mSwipeLayout.setRefreshing(true);
        mSubscription = RedditClient.getInstance().getTops(null)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RedditListingResponse>() {
                    @Override
                    public void onCompleted() {
                        mIsLoadingMore = false;
                        mSwipeLayout.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mIsLoadingMore = false;
                        mSwipeLayout.setRefreshing(false);
                    }

                    @Override
                    public void onNext(RedditListingResponse response) {
                        mAfterKey = response.data.after;
                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();
                        realm.delete(Feed.class);
                        for (RedditListingResponse.ListingData.FeedChildren feed : response.data.children) {
                            if (feed.data.preview != null && feed.data.preview.images != null && feed.data.preview.images.size() > 0) {
                                feed.data.sourcePictureUrl = feed.data.preview.images.get(0).source.url;
                            }
                            realm.copyToRealmOrUpdate(feed.data);
                        }
                        realm.commitTransaction();
                    }
                });
    }


    public void loadMoreItems() {
        mSwipeLayout.setRefreshing(true);
        mSubscription = RedditClient.getInstance().getTops(mAfterKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RedditListingResponse>() {
                    @Override
                    public void onCompleted() {
                        mIsLoadingMore = false;
                        mSwipeLayout.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mIsLoadingMore = false;
                        mSwipeLayout.setRefreshing(false);
                    }

                    @Override
                    public void onNext(RedditListingResponse response) {
                        mAfterKey = response.data.after;
                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();
                        for (RedditListingResponse.ListingData.FeedChildren feed : response.data.children) {
                            if (feed.data.preview != null && feed.data.preview.images != null && feed.data.preview.images.size() > 0) {
                                feed.data.sourcePictureUrl = feed.data.preview.images.get(0).source.url;
                            }
                            realm.copyToRealmOrUpdate(feed.data);
                        }
                        realm.commitTransaction();

                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
        mRealm.close();
    }

    @Override
    public void onRefresh() {
        initData();
    }
}
