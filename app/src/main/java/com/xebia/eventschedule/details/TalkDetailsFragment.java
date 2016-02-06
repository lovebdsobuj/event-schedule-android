package com.xebia.eventschedule.details;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xebia.eventschedule.R;
import com.xebia.eventschedule.model.Favorites;
import com.xebia.eventschedule.model.Speaker;
import com.xebia.eventschedule.model.Talk;
import com.xebia.eventschedule.util.LayoutUtils;

import java.util.List;

public class TalkDetailsFragment extends Fragment {

    private Talk mTalk;

    private TextView mTitleView;
    private TextView mTimeView;
    private TextView mRoomView;
    private TextView mAbstractView;
    private LinearLayout mContentView;
    private View mPlaceholderView;
    private Toolbar mToolbar;

    /* Suppress Lint warning about using the 'back arrow' from the AppCompat library resources */
    @SuppressLint("PrivateResource")
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
            mContentView = (LinearLayout) root.findViewById(R.id.content);
            mPlaceholderView = root.findViewById(R.id.placeholder);

            mToolbar = (Toolbar) root.findViewById(R.id.toolbar);
            mToolbar.inflateMenu(R.menu.activity_details);
            mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    final int itemId = menuItem.getItemId();
                    if (itemId == R.id.menu_add_favorite || itemId == R.id.menu_remove_favorite) {
                        onFavoriteClick();
                        return true;
                    }
                    return false;
                }
            });

            // enable back navigation on phone
            if (!LayoutUtils.isDualPane(getActivity())) {
                mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
                mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NavUtils.navigateUpFromSameTask(getActivity());
                    }
                });
            }
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
        } else {
            Favorites.get().add(mTalk);
        }
        Favorites.get().save(getActivity());

        updateFavoriteMenu(mTalk);
    }

    private void updateFavoriteMenu(final Talk talk) {
        MenuItem addFavorite = mToolbar.getMenu().findItem(R.id.menu_add_favorite);
        addFavorite.setVisible(!(Favorites.get().contains(talk) || talk.isAlwaysFavorite()) && !talk.isBreak());
        addFavorite.setEnabled(!talk.isAlwaysFavorite());
        MenuItem removeFavorite = mToolbar.getMenu().findItem(R.id.menu_remove_favorite);
        removeFavorite.setVisible((Favorites.get().contains(talk) || talk.isAlwaysFavorite()) && !talk.isBreak());
        removeFavorite.setEnabled(!talk.isAlwaysFavorite());
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
        mContentView.setVisibility(show ? View.GONE : View.VISIBLE);
        mPlaceholderView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void showTalk(Talk talk) {
        mTalk = talk;
        if (null == mTitleView) {
            // view not ready yet
            return;
        }
        showPlaceholder(false);
        mToolbar.setTitle(talk.getTitle());
        mTitleView.setText(talk.getTitle());
        mRoomView.setText(null != talk.getRoom() ? talk.getRoom().getName() : null);
        mAbstractView.setText(talk.getAbstract());

        if (null != talk.getSlot()) {
            mTimeView.setText(talk.getSlot().format(getActivity()));
            mTimeView.setVisibility(View.VISIBLE);
        } else {
            mTimeView.setVisibility(View.GONE);
        }

        updateFavoriteMenu(talk);
        showSpeakers(talk.getSpeakers());
    }

    private void showSpeakers(List<Speaker> speakers) {

        // remove any speaker views that are already displayed
        for (int childIndex = mContentView.getChildCount() - 1; childIndex > 2; childIndex--) {
            mContentView.removeViewAt(childIndex);
        }

        if (null == speakers) {
            return;
        }

        // Add a view for each speaker in the talk.
        for (final Speaker speaker : speakers) {
            SpeakerDetailsView view = new SpeakerDetailsView(getActivity());
            view.showSpeaker(speaker);
            mContentView.addView(view);

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
