package com.xebia.eventschedule.list;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
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
 * <p/>
 * Created by steven on 21-4-14.
 */
public class TalkListItemView extends RelativeLayout {

    private TextView mTitleView;
    private TextView mStartDateLabel;
    private TextView mStartDateView;
    private TextView mRoomLabel;
    private TextView mRoomView;
    private ParseImageView mSpeakerImage;
    private ImageButton mFavoriteButton;
    private View mHighlightMarker;

    private int mHeaderTextColor;
    private java.text.DateFormat mTimeFormat;
    private boolean mMasterDetailMode;

    private Talk mTalk;

    public TalkListItemView(Context context) {
        super(context);
    }

    public TalkListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressLint("NewApi")
    public TalkListItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTitleView = (TextView) findViewById(R.id.title);
        mStartDateLabel = (TextView) findViewById(R.id.start_date_label);
        mStartDateView = (TextView) findViewById(R.id.start_date);
        mRoomLabel = (TextView) findViewById(R.id.room_label);
        mRoomView = (TextView) findViewById(R.id.room);
        mSpeakerImage = (ParseImageView) findViewById(R.id.speakerimage);
        mFavoriteButton = (ImageButton) findViewById(R.id.favorite_button);
        mFavoriteButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                toggleFavorite();
            }
        });
        mHighlightMarker = findViewById(R.id.highlight);

        // load some context-related things
        mSpeakerImage.setPlaceholder(getResources().getDrawable(R.drawable.ic_speaker));
        mHeaderTextColor = getResources().getColor(R.color.header_text);
        mTimeFormat = DateFormat.getTimeFormat(getContext());
        mMasterDetailMode = LayoutUtils.isDualPane(getContext());
    }

    public void showTalk(final Talk talk) {
        mTalk = talk;
        updateBackground(talk);
        updateTextViews(talk);
        updateFavoriteButton(talk);
        updateSpeakerImage(talk);
        updateHighlight();
    }

    public void setHighlighted(boolean selected) {
        mTalk.setHighlighted(selected);
        updateHighlight();
    }

    private void updateHighlight() {
        mHighlightMarker.setVisibility(mMasterDetailMode && mTalk.isHighlighted() ? View.VISIBLE
                : View.GONE);
    }

    private void updateBackground(final Talk talk) {
        int bgResId;
        int highlightResId;
        if (talk.isBreak()) {
            bgResId = R.color.tint_color;
            highlightResId = R.color.base_color;
        } else if (talk.isKeynote()) {
            bgResId = R.color.base_color;
            highlightResId = R.color.tint_color;
        } else {
            bgResId = android.R.color.transparent;
            highlightResId = R.color.base_color;
        }
        this.setBackgroundResource(bgResId);
        mHighlightMarker.setBackgroundResource(highlightResId);
    }

    private void updateSpeakerImage(final Talk talk) {
        if (talk.isBreak()) {
            mSpeakerImage.setVisibility(View.INVISIBLE);
        } else {
            mSpeakerImage.setVisibility(View.VISIBLE);
            if (talk.getSpeakers() != null && !talk.getSpeakers().isEmpty()
                    && talk.getSpeakers().get(0).getPhoto() != null) {
                mSpeakerImage.setParseFile(talk.getSpeakers().get(0).getPhoto());
                mSpeakerImage.loadInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] data, ParseException e) {
                        if (e != null && e.getMessage() != null) {
                            Log.w(TalkListItemView.class.getSimpleName(),
                                    "Failed to load speaker image: " + e.getMessage());
                        }
                    }
                });
            }
        }
    }

    private void updateFavoriteButton(final Talk talk) {
        if (mMasterDetailMode || talk.isAlwaysFavorite()) {
            mFavoriteButton.setVisibility(View.GONE);
        } else {
            mFavoriteButton.setVisibility(View.VISIBLE);
            mFavoriteButton.setImageResource(Favorites.get().contains(talk) ? R.drawable
                    .ic_rating_important : R.drawable.ic_rating_not_important);
            mFavoriteButton.setFocusable(false);
        }
    }

    private void updateTextViews(final Talk talk) {
        mTitleView.setText(talk.getTitle());

        mStartDateView.setText(mTimeFormat.format(talk.getSlot().getStartTime()));
        mRoomView.setText(null != talk.getRoom() ? talk.getRoom().getName() : null);

        int textColor = talk.isBreak() ? Color.WHITE : mHeaderTextColor;
        mTitleView.setTextColor(textColor);
        mStartDateLabel.setTextColor(textColor);
        mStartDateView.setTextColor(textColor);
        mRoomLabel.setTextColor(textColor);
        mRoomView.setTextColor(textColor);
    }

    private void toggleFavorite() {
        if (null == mTalk) {
            return;
        }

        Favorites favorites = Favorites.get();
        if (favorites.contains(mTalk)) {
            favorites.remove(mTalk);
            mFavoriteButton.setImageResource(R.drawable.ic_rating_not_important);
        } else {
            favorites.add(mTalk);
            mFavoriteButton.setImageResource(R.drawable.ic_rating_important);
        }
        favorites.save(getContext());
    }
}
