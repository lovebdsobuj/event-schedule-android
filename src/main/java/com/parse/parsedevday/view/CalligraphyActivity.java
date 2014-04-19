package com.parse.parsedevday.view;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Activity that uses the default calligraphy font. Wraps the context with a CalligraphyContextWrapper.
 * <p/>
 * Created by steven on 19-4-14.
 */
public class CalligraphyActivity extends ActionBarActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }
}
