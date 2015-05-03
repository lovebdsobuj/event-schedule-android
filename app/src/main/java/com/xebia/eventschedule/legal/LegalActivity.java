package com.xebia.eventschedule.legal;

import android.os.Bundle;
import android.view.MenuItem;

import com.parse.ParseAnalytics;
import com.xebia.eventschedule.R;
import com.xebia.eventschedule.util.CalligraphyActivity;

public class LegalActivity extends CalligraphyActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_legal);
        ParseAnalytics.trackEventInBackground("OpenedLegalActivity");
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
