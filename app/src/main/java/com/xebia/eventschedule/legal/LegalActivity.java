package com.xebia.eventschedule.legal;

import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.view.MenuItem;

import com.parse.ParseAnalytics;
import com.xebia.eventschedule.EventScheduleApplication;
import com.xebia.eventschedule.R;
import com.xebia.eventschedule.util.CalligraphyActivity;

import java.util.Map;

public class LegalActivity extends CalligraphyActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_legal);

        Map<String, String> dimens = new ArrayMap<>(1);
        dimens.put("eventId", ((EventScheduleApplication) getApplication()).getParseEventId());
        ParseAnalytics.trackEventInBackground("OpenedLegalActivity", dimens);
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
}
