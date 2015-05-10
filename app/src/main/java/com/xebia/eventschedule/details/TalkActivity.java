package com.xebia.eventschedule.details;

import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.xebia.eventschedule.EventScheduleApplication;
import com.xebia.eventschedule.R;
import com.xebia.eventschedule.model.Talk;
import com.xebia.eventschedule.util.CalligraphyActivity;

import java.util.Map;

/**
 * An Activity to display information about a particular talk.
 */
public class TalkActivity extends CalligraphyActivity {

    private boolean destroyed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        String talkId = Talk.getTalkId(getIntent().getData());
        Map<String, String> dimens = new ArrayMap<>(2);
        dimens.put("talkId", talkId);
        dimens.put("eventId", ((EventScheduleApplication) getApplication()).getParseEventId());
        ParseAnalytics.trackEventInBackground("OpenedTalkActivity", dimens);

        Talk.getInBackground(talkId, new GetCallback<Talk>() {
            @Override
            public void done(final Talk talk, ParseException e) {

                if (destroyed) {
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // By interpreting the home tab as a back press, we get the platform-specific "previous
            // activity" transition instead of the "next activity" one.
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyed = true;
    }
}
