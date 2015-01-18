package com.xebia.eventschedule.eventdetails;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.xebia.eventschedule.R;
import com.xebia.eventschedule.util.CalligraphyActivity;

/**
 * Displays static information about XebiCon. What? When? Where?
 * <p/>
 * Created by steven on 26-4-14.
 */
public class EventDetailsActivity extends CalligraphyActivity {

    private static final String MAPS_ACTION = "geo:0,0?q=SS%20Rotterdam," +
            "%203e%20Katendrechtsehoofd%2025,%203072AM%20Rotterdam";
    private static final String DIALER_ACTION = "tel:+31355381921";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        findViewById(R.id.location_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMap();
            }
        });

        findViewById(R.id.contact_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialer();
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

    private void openDialer() {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(DIALER_ACTION));
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, getString(R.string.activity_not_found), Toast.LENGTH_LONG).show();
        }
    }

    private void openMap() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(MAPS_ACTION));
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, getString(R.string.activity_not_found), Toast.LENGTH_LONG).show();
        }
    }
}
