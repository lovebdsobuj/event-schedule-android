package com.xebia.eventschedule.details;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xebia.eventschedule.R;
import com.xebia.eventschedule.model.Favorites;
import com.xebia.eventschedule.model.Speaker;
import com.xebia.eventschedule.model.Talk;

import java.util.List;

public class TalkDetailsFragment extends Fragment {

    private Talk mTalk;

    private TextView mTitleView;
    private TextView mTimeView;
    private TextView mRoomView;
    private TextView mAbstractView;
    private ImageButton mFavoriteButton;
    private LinearLayout mScrollView;
    private View mHeaderView;
    private View mLogisticsView;
    private View mPlaceholderView;

    public TalkDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.talk_details, container, false);
        if (null != root) {
            // bind to views
            mTitleView = (TextView) root.findViewById(R.id.title);
            mTimeView = (TextView) root.findViewById(R.id.time);
            mRoomView = (TextView) root.findViewById(R.id.room);
            mAbstractView = (TextView) root.findViewById(R.id.talk_abstract);
            mFavoriteButton = (ImageButton) root.findViewById(R.id.favorite_button);
            mScrollView = (LinearLayout) root.findViewById(R.id.scroll_view);
            mHeaderView = root.findViewById(R.id.header);
            mLogisticsView = root.findViewById(R.id.logistics);
            mPlaceholderView = root.findViewById(R.id.placeholder);

            mFavoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onFavoriteClick();
                }
            });
        }

        setRetainInstance(true);

        return root;
    }

    private void onFavoriteClick() {
        if (null == mTalk) {
            return;
        }

        if (Favorites.get().contains(mTalk)) {
            Favorites.get().remove(mTalk);
            mFavoriteButton.setImageResource(R.drawable.ic_ab_favorite_unselected);
        } else {
            Favorites.get().add(mTalk);
            mFavoriteButton.setImageResource(R.drawable.ic_ab_favorite_selected);
        }
        Favorites.get().save(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != mTalk) {
            showTalk(mTalk);
        } else {
            showPlaceholder(true);
        }
    }

    private void showPlaceholder(boolean show) {
        mHeaderView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLogisticsView.setVisibility(show ? View.GONE : View.VISIBLE);
        mScrollView.setVisibility(show ? View.GONE : View.VISIBLE);
        mPlaceholderView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void showTalk(Talk talk) {
        mTalk = talk;
        if (null == mTitleView) {
            // view not ready yet
            return;
        }
        showPlaceholder(false);
        mTitleView.setText(talk.getTitle());
        mTimeView.setText(talk.getSlot().format(getActivity()));
        mRoomView.setText(null != talk.getRoom() ? talk.getRoom().getName() : null);
        mAbstractView.setText(talk.getAbstract());

        if (Favorites.get().contains(talk)) {
            mFavoriteButton.setImageResource(R.drawable.ic_ab_favorite_selected);
        } else {
            mFavoriteButton.setImageResource(R.drawable.ic_ab_favorite_unselected);
        }
        if (!talk.isAlwaysFavorite()) {
            mFavoriteButton.setVisibility(View.VISIBLE);
        }

        showSpeakers(talk.getSpeakers());
    }

    private void showSpeakers(List<Speaker> speakers) {

        // remove any speaker views that are already displayed
        for (int childIndex = mScrollView.getChildCount() - 1; childIndex > 0; childIndex--) {
            mScrollView.removeViewAt(childIndex);
        }

        if (null == speakers) {
            return;
        }

        // Add a view for each speaker in the talk.
        for (final Speaker speaker : speakers) {
            SpeakerDetailsView view = new SpeakerDetailsView(getActivity());
            view.showSpeaker(speaker);
            mScrollView.addView(view);

            if (!TextUtils.isEmpty(speaker.getTwitter())) {
                view.setOnTwitterClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showTwitterProfile(speaker);
                    }
                });
            }
        }
    }

    private void showTwitterProfile(Speaker speaker) {
        String twitter = speaker.getTwitter();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + twitter)));
        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/intent/user?screen_name=" + twitter)));
        }
    }
}
