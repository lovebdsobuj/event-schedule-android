package com.xebia.xebicon2014.about;

import android.os.Bundle;
import android.view.MenuItem;

import com.xebia.xebicon2014.R;
import com.xebia.xebicon2014.view.CalligraphyActivity;

/**
 * Displays static information about XebiCon. What? When? Where?
 * <p/>
 * Created by steven on 26-4-14.
 */
public class AboutActivity extends CalligraphyActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
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
