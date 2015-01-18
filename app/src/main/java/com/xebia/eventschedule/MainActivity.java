package com.xebia.eventschedule;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;

import com.xebia.eventschedule.eventdetails.EventDetailsActivity;
import com.xebia.eventschedule.details.TalkActivity;
import com.xebia.eventschedule.details.TalkDetailsFragment;
import com.xebia.eventschedule.legal.LegalActivity;
import com.xebia.eventschedule.list.TalkListFragment;
import com.xebia.eventschedule.model.Talk;
import com.xebia.eventschedule.util.CalligraphyActivity;
import com.xebia.eventschedule.util.LayoutUtils;

/**
 * An Activity with a tabs for the complete schedule and the list of favorited talks. This was
 * originally created from an ADT wizard.
 */
public class MainActivity extends CalligraphyActivity implements TalkListFragment.Listener {

    public static Intent createIntent(Context context){
        Intent intent = new Intent(context, MainActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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
        } else if (item.getItemId() == R.id.event_details) {
            startActivity(new Intent(this, EventDetailsActivity.class));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onTalkClick(Talk talk) {

        if (LayoutUtils.isDualPane(this)) {
            Fragment f = getSupportFragmentManager().findFragmentById(R.id.talk_details);
            ((TalkDetailsFragment) f).showTalk(talk);
        } else {
            Intent intent = new Intent(this, TalkActivity.class);
            intent.setData(talk.getUri());
            startActivity(intent);
        }
    }
}
