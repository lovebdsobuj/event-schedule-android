package com.xebia.xebicon2014.details;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.ParseImageView;
import com.xebia.xebicon2014.R;
import com.xebia.xebicon2014.model.Speaker;

/**
 * View group for speaker details.
 * <p/>
 * Created by steven on 4-5-14.
 */
public class SpeakerDetailsView extends LinearLayout {

    private ParseImageView mPhotoView;
    private TextView mNameView;
    private TextView mTitleView;
    private TextView mCompanyView;
    private TextView mBioView;
    private TextView mTwitterView;

    private OnClickListener mTwitterListener;

    public SpeakerDetailsView(Context context) {
        super(context);
    }

    public SpeakerDetailsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressLint("NewApi")
    public SpeakerDetailsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
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

        mPhotoView.setPlaceholder(getResources().getDrawable(R.drawable.ic_speaker));

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
        mTitleView.setText(speaker.getTitle());
        mCompanyView.setText(speaker.getCompany());

        if (TextUtils.isEmpty(speaker.getTwitter())) {
            mTwitterView.setVisibility(View.GONE);
        } else {
            String twitter = getResources().getString(R.string.twitter_handle, speaker.getTwitter());
            mTwitterView.setVisibility(View.VISIBLE);
            mTwitterView.setText(twitter);
        }

        String bio = speaker.getBio();
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
