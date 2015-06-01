package com.xebia.eventschedule.details;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.xebia.eventschedule.EventScheduleApplication;
import com.xebia.eventschedule.R;
import com.xebia.eventschedule.model.Talk;
import com.xebia.eventschedule.util.AnalyticsHelper;
import com.xebia.eventschedule.util.CalligraphyActivity;

/**
 * An Activity to display information about a particular talk.
 */
public class TalkActivity extends CalligraphyActivity {

    private boolean mDestroyed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        String talkId = Talk.getTalkId(getIntent().getData());

        AnalyticsHelper
                .openedTalkDetailsActivity(((EventScheduleApplication) getApplication()).getParseEventId(), talkId);

        Talk.getInBackground(talkId, new GetCallback<Talk>() {
            @Override
            public void done(final Talk talk, ParseException e) {

                if (mDestroyed) {
                    // do not perform fragment transaction on destroyed activity
                    return;
                }

                findViewById(R.id.loading_indicator).setVisibility(View.GONE);

                // if we cannot get the data right now, the best we can do is show a toast.
                if (e != null) {
                    Toast.makeText(TalkActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }

                if (talk == null) {
                    throw new RuntimeException("Somehow the talk was null.");
                }

                TalkDetailsFragment fragment = new TalkDetailsFragment();
                getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment)
                    .commitAllowingStateLoss();
                fragment.showTalk(talk);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDestroyed = true;
    }
}
