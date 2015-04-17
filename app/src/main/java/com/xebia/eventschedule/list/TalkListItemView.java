package com.xebia.eventschedule.list;

import android.annotation.SuppressLint;
import android.content.Context;
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
 *
 * Created by steven on 21-4-14.
 */
public class TalkListItemView extends RelativeLayout implements ScheduleListItemView {

    private TextView mTitleView;
    private TextView mStartDateView;
    private TextView mRoomView;
    private ParseImageView mSpeakerImage;
    private ImageButton mFavoriteButton;
    private View mHighlightMarker;

    private int mPrimaryColor;
    private int mTextColor;
    private int mSecondaryColor;

    private java.text.DateFormat mTimeFormat;
    private boolean mMasterDetailMode;
    private Talk mTalk;

    public TalkListItemView(Context context) {
        super(context);
    }

    public TalkListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TalkListItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @SuppressLint("NewApi")
    public TalkListItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTitleView = (TextView) findViewById(R.id.title);
        mStartDateView = (TextView) findViewById(R.id.start_date);
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
        mPrimaryColor = getResources().getColor(R.color.primary);
        mTextColor = getResources().getColor(R.color.text);
        mSecondaryColor = getResources().getColor(R.color.textSecondary);
        mTimeFormat = DateFormat.getTimeFormat(getContext());
        mMasterDetailMode = LayoutUtils.isDualPane(getContext());
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
            mFavoriteButton.setImageResource(Favorites.get().contains(talk) ? R.drawable.ic_rating_important
                    : R.drawable.ic_rating_not_important);
            mFavoriteButton.setFocusable(false);
        }
    }

    private void updateTextViews(final Talk talk) {
        mTitleView.setText(talk.getTags().size() > 0
                ? talk.getTitle() + " " + talk.getTags().get(0)
                : talk.getTitle());

        mStartDateView.setText(mTimeFormat.format(talk.getSlot().getStartTime()));
        mRoomView.setText(null != talk.getRoom() ? talk.getRoom().getName() : null);

        int textColor = talk.isKeynote() ? mPrimaryColor : mTextColor;
        int secondaryColor = talk.isKeynote() ? mPrimaryColor : mSecondaryColor;
        mTitleView.setTextColor(textColor);
        mStartDateView.setTextColor(secondaryColor);
        mRoomView.setTextColor(secondaryColor);
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
