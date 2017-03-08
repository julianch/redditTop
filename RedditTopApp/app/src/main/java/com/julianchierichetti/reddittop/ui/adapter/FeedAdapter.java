package com.julianchierichetti.reddittop.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.julianchierichetti.reddittop.R;
import com.julianchierichetti.reddittop.model.Feed;

import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;

/**
 * Created by julian on 07-Mar-17.
 */

public class FeedAdapter extends RealmBaseAdapter<Feed> implements ListAdapter {


    public FeedAdapter(Context context, OrderedRealmCollection<Feed> data) {
        super(context, data);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_feed, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (adapterData != null) {
            final Feed item = adapterData.get(position);
            viewHolder.mAuthor.setText(String.format(context.getString(R.string.feed_item_author), item.author));
            viewHolder.mTitle.setText(item.title);
            viewHolder.mComments.setText(String.format(context.getString(R.string.feed_item_comments), item.num_comments));
            if (validThumbnail(item.thumbnail)) {
                Picasso.with(context).load(item.thumbnail).into(viewHolder.mThumbnail);
                viewHolder.mThumbnail.setVisibility(View.VISIBLE);
            } else {
                viewHolder.mThumbnail.setVisibility(View.GONE);
            }
            Date date = new Date();
            date.setTime(item.created_utc * 1000);
            PrettyTime prettyTime = new PrettyTime();
            viewHolder.mDate.setText(prettyTime.format(date));
            viewHolder.mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (validThumbnail(item.sourcePictureUrl)) {
                        handleClick(item.sourcePictureUrl);
                    }
                }
            });

        }
        return convertView;
    }

    private void handleClick(String url){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        context.startActivity(i);
    }

    private boolean validThumbnail(String thumbnail) {
        return thumbnail != null && !thumbnail.isEmpty() && Patterns.WEB_URL.matcher(thumbnail).matches();
    }

    public static class ViewHolder {

        @BindView(R.id.item_feed_author)
        TextView mAuthor;
        @BindView(R.id.item_feed_title)
        TextView mTitle;
        @BindView(R.id.item_feed_thumbnail)
        ImageView mThumbnail;
        @BindView(R.id.item_feed_comments)
        TextView mComments;
        @BindView(R.id.item_feed_date)
        TextView mDate;
        @BindView(R.id.item_feed_card_view)
        CardView mCardView;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}