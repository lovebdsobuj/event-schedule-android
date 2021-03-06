package com.xebia.eventschedule.list;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xebia.eventschedule.R;
import com.xebia.eventschedule.model.Favorites;
import com.xebia.eventschedule.model.Tags;
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
    private View mTagView;
    private ImageView mSpeakerImage;
    private ImageView mFavoriteButton;
    private View mSelectionIndicator;

    private int mPrimaryColor;
    private int mTextColor;
    private int mSecondaryColor;

    private java.text.DateFormat mTimeFormat;
    private boolean mMasterDetailMode;
    private Talk mTalk;
    private TextView mSpeakersView;
    private int mAccentColor;

    public TalkListItemView(final Context context) {
        super(context);
        initialize();
    }

    public TalkListItemView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public TalkListItemView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        LayoutInflater.from(getContext()).inflate(R.layout.list_item_talk, this);
        onFinishInflate();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTitleView = (TextView) findViewById(R.id.title);
        mStartDateView = (TextView) findViewById(R.id.start_date);
        mRoomView = (TextView) findViewById(R.id.room);
        mSpeakersView = (TextView) findViewById(R.id.speakers);
        mTagView = findViewById(R.id.tag);
        mSpeakerImage = (ImageView) findViewById(R.id.speakerimage);
        mFavoriteButton = (ImageView) findViewById(R.id.favorite);
        mSelectionIndicator = findViewById(R.id.selection_indicator);

        // load some context-related things
        mPrimaryColor = ContextCompat.getColor(getContext(), R.color.primary);
        mTextColor = ContextCompat.getColor(getContext(), R.color.text);
        mSecondaryColor = ContextCompat.getColor(getContext(), R.color.textSecondary);
        mAccentColor = ContextCompat.getColor(getContext(), R.color.accent);
        mTimeFormat = DateFormat.getTimeFormat(getContext());
        mMasterDetailMode = LayoutUtils.isDualPane(getContext());
    }

    @Override
    public void showTalk(final Talk talk) {
        mTalk = talk;
        updateTextViews(talk);
        updateFavoriteButton(talk);
        updateSpeakerImage(talk);
        updateSelectionIndicator();

        Tags tags = Tags.get();
        if (talk.getTags().size() > 0 && null != tags) {
            mTagView.setVisibility(View.VISIBLE);
            mTagView.setBackgroundColor(tags.getTagColor(talk.getTags().get(0)));
        } else {
            mTagView.setVisibility(View.GONE);
        }
    }

    private void updateSelectionIndicator() {
        if (mMasterDetailMode) {
            mSelectionIndicator.setVisibility(mTalk.isSelected() ? View.VISIBLE : View.GONE);
        }

        Tags tags = Tags.get();
        if (mTalk.getTags().size() > 0 && null != tags) {
            mSelectionIndicator.setBackgroundColor(tags.getTagColor(mTalk.getTags().get(0)));
        } else {
            mSelectionIndicator.setBackgroundColor(mAccentColor);
        }
    }

    private void updateSpeakerImage(final Talk talk) {
        if (talk.getSpeakers() != null && !talk.getSpeakers().isEmpty()) {
            mSpeakersView.setText(TextUtils.join(", ", talk.getSpeakers()));
            mSpeakersView.setVisibility(View.VISIBLE);
            Picasso
                .with(getContext())
                .load(talk.getSpeakers().get(0).getPhotoURL())
                .config(Bitmap.Config.RGB_565)
                .resizeDimen(R.dimen.schedule_speaker_photo_width, R.dimen.schedule_speaker_photo_height)
                .centerCrop()
                .placeholder(R.drawable.speaker_placeholder)
                .into(mSpeakerImage);
        } else {
            mSpeakersView.setText(null);
            mSpeakersView.setVisibility(View.GONE);
            Picasso
                .with(getContext())
                .load(R.drawable.speaker_placeholder)
                .config(Bitmap.Config.RGB_565)
                .resizeDimen(R.dimen.schedule_speaker_photo_width, R.dimen.schedule_speaker_photo_height)
                .centerCrop()
                .into(mSpeakerImage);
        }
    }

    private void updateFavoriteButton(final Talk talk) {
        if (talk.isAlwaysFavorite()) {
            mFavoriteButton.setVisibility(View.VISIBLE);
        } else {
            mFavoriteButton.setVisibility(Favorites.get().contains(talk) ? View.VISIBLE : View.GONE);
        }
    }

    private void updateTextViews(final Talk talk) {
        mTitleView.setText(talk.getTitle());

        if (null != talk.getSlot() && null != talk.getSlot().getStartTime()) {
            mStartDateView.setText(mTimeFormat.format(talk.getSlot().getStartTime()));
        }
        mRoomView.setText(null != talk.getRoom() ? talk.getRoom().getName() : null);

        int textColor = talk.isKeynote() ? mPrimaryColor : mTextColor;
        int secondaryColor = talk.isKeynote() ? mPrimaryColor : mSecondaryColor;
        mTitleView.setTextColor(textColor);
        mSpeakersView.setTextColor(textColor);
        mStartDateView.setTextColor(secondaryColor);
        mRoomView.setTextColor(secondaryColor);
    }
}
