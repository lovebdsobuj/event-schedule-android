package com.xebia.xebicon2014;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.ParseAnalytics;
import com.xebia.xebicon2014.about.AboutActivity;
import com.xebia.xebicon2014.details.TalkActivity;
import com.xebia.xebicon2014.details.TalkDetailsFragment;
import com.xebia.xebicon2014.legal.LegalActivity;
import com.xebia.xebicon2014.list.TalkListFragment;
import com.xebia.xebicon2014.model.Talk;
import com.xebia.xebicon2014.util.CalligraphyActivity;

/**
 * An Activity with a tabs for the complete schedule and the list of favorited talks. This was
 * originally created from an ADT wizard.
 */
public class MainActivity extends CalligraphyActivity implements TalkListFragment.Listener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Tracks app-open metrics using Parse.
        ParseAnalytics.trackAppOpened(getIntent());

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu. This adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.legal) {
            startActivity(new Intent(this, LegalActivity.class));
            return true;
        } else if (item.getItemId() == R.id.about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onTalkClick(Talk talk) {

        Fragment f = getSupportFragmentManager().findFragmentById(R.id.talk_details);
        if (null != f) {
            ((TalkDetailsFragment) f).showTalk(talk);
        } else {
            Intent intent = new Intent(this, TalkActivity.class);
            intent.setData(talk.getUri());
            startActivity(intent);
        }
    }
}
