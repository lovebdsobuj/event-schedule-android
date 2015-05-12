package com.xebia.eventschedule.details;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xebia.eventschedule.R;
import com.xebia.eventschedule.model.Speaker;

/**
 * View group for speaker details.
 *
 * Created by steven on 4-5-14.
 */
public class SpeakerDetailsView extends FrameLayout {

    /**
     * Bug #28: Weird stuff gets linkified. Turns out this is because of &lt;nbsp&gt; characters in
     * the input. This filter rejects matched URLs that contain a non-breaking space.
     */
    private static final Linkify.MatchFilter REJECT_NBSP = new Linkify.MatchFilter() {
        private static final char NBSP = (char) 0xa0;
        @Override
        public boolean acceptMatch(CharSequence s, int start, int end) {
            for (int i = start; i < end; i++) {
                if (s.charAt(i) == NBSP) {
                    return false;
                }
            }
            return true;
        }
    };
    private ImageView mPhotoView;
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
        mPhotoView = (ImageView) findViewById(R.id.photo);
        mNameView = (TextView) findViewById(R.id.name);
        mTitleView = (TextView) findViewById(R.id.title);
        mCompanyView = (TextView) findViewById(R.id.company);
        mTwitterView = (TextView) findViewById(R.id.twitter);
        mBioView = (TextView) findViewById(R.id.bio);

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
        Picasso
            .with(getContext())
            .load(speaker.getPhotoURL())
            .config(Bitmap.Config.RGB_565)
            .resizeDimen(R.dimen.schedule_speaker_photo_width, R.dimen.schedule_speaker_photo_height)
            .centerCrop()
            .placeholder(R.drawable.speaker_placeholder)
            .into(mPhotoView);
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
            Linkify.addLinks(mBioView, Patterns.WEB_URL, "http://", REJECT_NBSP, null);
        }
    }

    public void setOnTwitterClickListener(OnClickListener listener) {
        mTwitterListener = listener;
    }
}
