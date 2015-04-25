package com.xebia.eventschedule.list;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.xebia.eventschedule.R;
import com.xebia.eventschedule.model.Favorites;
import com.xebia.eventschedule.model.Talk;
import com.xebia.eventschedule.util.LayoutUtils;

/**
 * View group for talk list items. Lets me have my cake and eat it too.
 *
 * Created by steven on 21-4-14.
 */
public class TalkListItemView extends RelativeLayout implements ScheduleListItemView {

    private final TalkListClickListener mListener;
    private TextView mTitleView;
    private TextView mStartDateView;
    private TextView mRoomView;
    private ParseImageView mSpeakerImage;
    private ImageView mFavoriteButton;
    private View mHighlightMarker;

    private int mPrimaryColor;
    private int mTextColor;
    private int mSecondaryColor;

    private java.text.DateFormat mTimeFormat;
    private boolean mMasterDetailMode;
    private Talk mTalk;
    private Drawable mSpeakerPlaceholder;
    private TextView mSpeakersView;

    public TalkListItemView(final Context context, final TalkListClickListener listener) {
        super(context);
        mListener = listener;
        LayoutInflater.from(context).inflate(R.layout.list_item_talk, this);
        onFinishInflate();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTitleView = (TextView) findViewById(R.id.title);
        mStartDateView = (TextView) findViewById(R.id.start_date);
        mRoomView = (TextView) findViewById(R.id.room);
        mSpeakersView = (TextView) findViewById(R.id.speakers);
        mSpeakerImage = (ParseImageView) findViewById(R.id.speakerimage);
        mFavoriteButton = (ImageView) findViewById(R.id.favorite);
        mHighlightMarker = findViewById(R.id.highlight);

        // load some context-related things
        mPrimaryColor = getResources().getColor(R.color.primary);
        mTextColor = getResources().getColor(R.color.text);
        mSecondaryColor = getResources().getColor(R.color.textSecondary);
        mTimeFormat = DateFormat.getTimeFormat(getContext());
        mMasterDetailMode = LayoutUtils.isDualPane(getContext());

        mSpeakerPlaceholder = getResources().getDrawable(R.drawable.speaker_placeholder);
        mSpeakerImage.setPlaceholder(mSpeakerPlaceholder);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick();
            }
        });
    }

    @Override
    public void showTalk(final Talk talk) {
        mTalk = talk;
        updateTextViews(talk);
        updateFavoriteButton(talk);
        updateSpeakerImage(talk);
        updateHighlight();
    }

    @Override
    public void setHighlighted(boolean selected) {
        mTalk.setHighlighted(selected);
        updateHighlight();
    }

    private void updateHighlight() {
        mHighlightMarker.setVisibility(mMasterDetailMode && mTalk.isHighlighted() ? View.VISIBLE : View.GONE);
    }

    private void updateSpeakerImage(final Talk talk) {
        if (talk.getSpeakers() != null && !talk.getSpeakers().isEmpty()) {
            mSpeakersView.setText(TextUtils.join(",", talk.getSpeakers()));
            mSpeakersView.setVisibility(View.VISIBLE);
            if (talk.getSpeakers().get(0).getPhoto() != null) {
                mSpeakerImage.setParseFile(talk.getSpeakers().get(0).getPhoto());
                mSpeakerImage.loadInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] data, ParseException e) {
                        if (e != null && e.getMessage() != null) {
                            mSpeakerImage.setImageDrawable(mSpeakerPlaceholder);
                            Log.w(TalkListItemView.class.getSimpleName(),
                                "Failed to load speaker image: " + e.getMessage());
                        }
                    }
                });
            } else {
                mSpeakerImage.setImageDrawable(mSpeakerPlaceholder);
            }
        } else {
            mSpeakersView.setText(null);
            mSpeakersView.setVisibility(View.GONE);
            mSpeakerImage.setImageDrawable(mSpeakerPlaceholder);
        }
    }

    private void updateFavoriteButton(final Talk talk) {
        if (mMasterDetailMode || talk.isAlwaysFavorite()) {
            mFavoriteButton.setVisibility(View.VISIBLE);
        } else {
            mFavoriteButton.setVisibility(Favorites.get().contains(talk) ? View.VISIBLE : View.GONE);
        }
    }

    private void updateTextViews(final Talk talk) {
        mTitleView.setText(talk.getTitle());

        mStartDateView.setText(mTimeFormat.format(talk.getSlot().getStartTime()));
        mRoomView.setText(null != talk.getRoom() ? talk.getRoom().getName() : null);

        int textColor = talk.isKeynote() ? mPrimaryColor : mTextColor;
        int secondaryColor = talk.isKeynote() ? mPrimaryColor : mSecondaryColor;
        mTitleView.setTextColor(textColor);
        mSpeakersView.setTextColor(textColor);
        mStartDateView.setTextColor(secondaryColor);
        mRoomView.setTextColor(secondaryColor);
    }

    public void onItemClick() {
        setHighlighted(true);
        if (null != mListener) {
            mListener.onTalkClick(mTalk);
        }
    }
}
