package com.xebia.xebicon2014.util;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;

import com.xebia.xebicon2014.R;
import com.xebia.xebicon2014.util.TypefaceSpan;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Activity that uses the default calligraphy font. Wraps the context with a CalligraphyContextWrapper.
 * <p/>
 * Created by steven on 19-4-14.
 */
public abstract class CalligraphyActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTitle();
    }

    /**
     * Sets the custom action bar title view.
     * <p/>
     * Source: http://www.tristanwaddington.com/2013/03/styling-the-android-action-bar-with-a-custom-font/
     */
    private void initTitle() {
        SpannableString title = new SpannableString(getString(R.string.app_name));
        String fontFamily = "FuturaStd-Heavy.otf";
        int textColor = getResources().getColor(R.color.actionbar_text);
        title.setSpan(new TypefaceSpan(this, fontFamily, textColor), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Update the action bar title with the TypefaceSpan instance
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(title);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase, R.attr.calligraphy));
    }
}
