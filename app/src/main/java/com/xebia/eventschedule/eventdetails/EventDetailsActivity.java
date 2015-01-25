package com.xebia.eventschedule.eventdetails;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.xebia.eventschedule.EventScheduleApplication;
import com.xebia.eventschedule.R;
import com.xebia.eventschedule.model.Event;
import com.xebia.eventschedule.util.CalligraphyActivity;

/**
 * Displays static information about XebiCon. What? When? Where?
 *
 * Created by steven on 26-4-14.
 */
public class EventDetailsActivity extends CalligraphyActivity {

    private boolean mDestroyed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        // Fetch the data about this talk from Parse.
        String eventId = ((EventScheduleApplication) getApplicationContext()).getParseEventId();
        Event.getInBackground(eventId, new GetCallback<Event>() {
            @Override
            public void done(final Event event, ParseException e) {

                if (mDestroyed) {
                    // do not perform fragment transaction on destroyed activity
                    return;
                }

                findViewById(R.id.loading_indicator).setVisibility(View.GONE);

                // if we cannot get the data right now, the best we can do is show a toast.
                if (e != null) {
                    Toast.makeText(EventDetailsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }

                if (event == null) {
                    throw new RuntimeException("Somehow the event was null.");
                }

                EventDetailsFragment fragment = EventDetailsFragment.newInstance(event);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, fragment)
                        .commitAllowingStateLoss();
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
        mDestroyed = true;
    }
}
