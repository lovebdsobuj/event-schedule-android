package com.xebia.eventschedule.details;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.parse.ParseImageView;
import com.xebia.eventschedule.R;
import com.xebia.eventschedule.model.Speaker;

/**
 * View group for speaker details.
 * <p/>
 * Created by steven on 4-5-14.
 */
public class SpeakerDetailsView extends FrameLayout {

    private ParseImageView mPhotoView;
    private TextView mNameView;
    private TextView mTitleView;
    private TextView mCompanyView;
    private TextView mBioView;
    private TextView mTwitterView;

    private OnClickListener mTwitterListener;

    public SpeakerDetailsView(Context context) {
        super(context);
        init();
    }

    public SpeakerDetailsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @SuppressLint("NewApi")
    public SpeakerDetailsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.list_item_speaker, this);
        onFinishInflate();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mPhotoView = (ParseImageView) findViewById(R.id.photo);
        mNameView = (TextView) findViewById(R.id.name);
        mTitleView = (TextView) findViewById(R.id.title);
        mCompanyView = (TextView) findViewById(R.id.company);
        mTwitterView = (TextView) findViewById(R.id.twitter);
        mBioView = (TextView) findViewById(R.id.bio);

        mPhotoView.setPlaceholder(getResources().getDrawable(R.drawable.speaker_placeholder));

        mTwitterView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mTwitterListener) {
                    mTwitterListener.onClick(view);
                }
            }
        });
    }

    public void showSpeaker(Speaker speaker) {
        mPhotoView.setParseFile(speaker.getPhoto());
        mPhotoView.loadInBackground();
        mNameView.setText(speaker.getName());

        final String title = speaker.getTitle();
        if (TextUtils.isEmpty(title)) {
            mTitleView.setVisibility(View.GONE);
        } else {
            mTitleView.setVisibility(View.VISIBLE);
            mTitleView.setText(title);
        }

        final String company = speaker.getCompany();
        if (TextUtils.isEmpty(company)) {
            mCompanyView.setVisibility(View.GONE);
        } else {
            mCompanyView.setVisibility(View.VISIBLE);
            mCompanyView.setText(company);
        }

        final String twitter = speaker.getTwitter();
        if (TextUtils.isEmpty(twitter)) {
            mTwitterView.setVisibility(View.GONE);
        } else {
            mTwitterView.setVisibility(View.VISIBLE);
            String twitterHandle = getResources().getString(R.string.twitter_handle, speaker.getTwitter());
            mTwitterView.setText(twitterHandle);
        }

        final String bio = speaker.getBio();
        if (TextUtils.isEmpty(bio)) {
            mBioView.setVisibility(View.GONE);
        } else {
            mBioView.setVisibility(View.VISIBLE);
            mBioView.setText(speaker.getBio());
        }
    }

    public void setOnTwitterClickListener(OnClickListener listener) {
        mTwitterListener = listener;
    }
}
