package com.xebia.xebicon2014.details;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xebia.xebicon2014.R;
import com.xebia.xebicon2014.model.Favorites;
import com.xebia.xebicon2014.model.Speaker;
import com.xebia.xebicon2014.model.Talk;

import java.util.List;

public class TalkDetailsFragment extends Fragment {

    private Talk mTalk;

    private TextView mTitleView;
    private TextView mTimeView;
    private TextView mRoomView;
    private TextView mAbstractView;
    private ImageButton mFavoriteButton;
    private LinearLayout mScrollView;

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

            mFavoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onFavoriteClick();
                }
            });
        }
        return root;
    }

    private void onFavoriteClick() {
        if (null == mTalk) {
            return;
        }

        if (Favorites.get().contains(mTalk)) {
            Favorites.get().remove(mTalk);
            mFavoriteButton.setImageResource(R.drawable.ic_rating_not_important_light);
        } else {
            Favorites.get().add(mTalk);
            mFavoriteButton.setImageResource(R.drawable.ic_rating_important_light);
        }
        Favorites.get().save(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != mTalk) {
            showTalk(mTalk);
        }
    }

    public void showTalk(Talk talk) {
        mTalk = talk;
        if (null == mTitleView) {
            // view not ready yet
            return;
        }
        mTitleView.setText(talk.getTitle());
        mTimeView.setText(talk.getSlot().format(getActivity()));
        mRoomView.setText(talk.getRoom().getName());
        mAbstractView.setText(talk.getAbstract());

        if (Favorites.get().contains(talk)) {
            mFavoriteButton.setImageResource(R.drawable.ic_rating_important_light);
        } else {
            mFavoriteButton.setImageResource(R.drawable.ic_rating_not_important_light);
        }
        if (!talk.isAlwaysFavorite()) {
            mFavoriteButton.setVisibility(View.VISIBLE);
        }

        showSpeakers(talk.getSpeakers());
    }

    private void showSpeakers(List<Speaker> speakers) {

        if (null == speakers) {
            return;
        }

        // Add a view for each speaker in the talk.
        for (Speaker speaker : speakers) {
            SpeakerDetailsView view = (SpeakerDetailsView) View.inflate(getActivity(),
                    R.layout.list_item_speaker, null);
            view.showSpeaker(speaker);
            mScrollView.addView(view);
        }
    }
}
