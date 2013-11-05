package com.parse.parsedevday.view;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.parsedevday.R;
import com.parse.parsedevday.model.Favorites;
import com.parse.parsedevday.model.Talk;

import android.content.Context;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * An ArrayAdapter to handle a list of Talks.
 */
public class TalkListAdapter extends ArrayAdapter<Talk> {
    public TalkListAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        // This is all pretty standard code for setting up an Android view.

        if (view == null) {
            view = View.inflate(getContext(), R.layout.list_item_talk, null);
        }

        final Talk talk = getItem(position);

        TextView startDateView = (TextView) view.findViewById(R.id.start_date);
        startDateView.setText(DateFormat.getTimeFormat(getContext()).format(talk.getSlot().getStartTime()));

        TextView endDateView = (TextView) view.findViewById(R.id.end_date);
        endDateView.setText(DateFormat.getTimeFormat(getContext()).format(talk.getSlot().getEndTime()));

        TextView titleView = (TextView) view.findViewById(R.id.title);
        TextView roomView = (TextView) view.findViewById(R.id.room);

        ParseImageView speakerimage = (ParseImageView) view.findViewById(R.id.speakerimage);
        // The placeholder will be used before and during the fetch, to be
        // replaced by the fetched image
        // data.

        titleView.setText(talk.getTitle());
        roomView.setText(talk.getRoom().getName());

        final ImageButton favoriteButton = (ImageButton) view.findViewById(R.id.favorite_button);
        if (Favorites.get().contains(talk)) {
            favoriteButton.setImageResource(R.drawable.light_rating_important);
        } else {
            favoriteButton.setImageResource(R.drawable.light_rating_not_important);
        }
        favoriteButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Favorites favorites = Favorites.get();
                if (favorites.contains(talk)) {
                    favorites.remove(talk);
                    favoriteButton.setImageResource(R.drawable.light_rating_not_important);
                } else {
                    favorites.add(talk);
                    favoriteButton.setImageResource(R.drawable.light_rating_important);
                }
                favorites.save(getContext());
            }
        });
        favoriteButton.setFocusable(false);

        if (talk.isAlwaysFavorite()) {
            favoriteButton.setVisibility(View.GONE);
        } else {
            favoriteButton.setVisibility(View.VISIBLE);
        }

        if (talk.isAlwaysFavorite()) {
            view.setBackgroundColor(Color.rgb(255, 255, 245));
        } else {
            view.setBackgroundColor(Color.rgb(245, 245, 245));
        }
        speakerimage.setPlaceholder(this.getContext().getResources().getDrawable(R.drawable.ic_launcher));
        if (talk != null && talk.getSpeakers() != null && !talk.getSpeakers().isEmpty()
                        && talk.getSpeakers().get(0).getPhoto() != null) {
            speakerimage.setParseFile(talk.getSpeakers().get(0).getPhoto());
            speakerimage.loadInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    if (data != null && e != null && e.getMessage() != null) {
                        Log.i("ParseImageView", "Fetched! Data length: " + data.length + ", or exception: " + e.getMessage());
                    }
                }
            });
        }
        return view;
    }
}
