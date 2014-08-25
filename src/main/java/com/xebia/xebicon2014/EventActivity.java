package com.xebia.xebicon2014;

import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import com.parse.GetCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.xebia.xebicon2014.list.EventListFragment;
import com.xebia.xebicon2014.model.Event;
import com.xebia.xebicon2014.util.CalligraphyActivity;

/**
 * An Activity with a tabs for the complete schedule and the list of favorited talks. This was
 * originally created from an ADT wizard.
 */
public class EventActivity extends CalligraphyActivity implements EventListFragment.Listener {
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        // Tracks app-open metrics using Parse.
        ParseAnalytics.trackAppOpened(getIntent());

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //drawerLayout.openDrawer(Gravity.LEFT);
        drawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.app_name,  /* "open drawer" description */
                R.string.app_name  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getActionBar().setTitle("");
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActionBar().setTitle("Events");
            }
        };

        // Set the drawer toggle as the DrawerListener
        drawerLayout.setDrawerListener(drawerToggle);
        //getActionBar().setBackgroundDrawable(new BitmapDrawable() {);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

            //ab_transparent_xcs.9.png

        String selectedEventId = ((XebiConApp) getApplicationContext()).getDataStore().getEventId();
        if (selectedEventId != null) {
            Event.getInBackground(selectedEventId, new GetCallback<Event>() {
                @Override
                public void done(Event event, ParseException e) {
                    if (e == null) {
                        onEventClick(event);
                    }
                }
            });
        }

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onEventClick(Event event) {
        ((XebiConApp) getApplicationContext()).getDataStore().setNewSelectedEvent(event);
        startActivity(MainActivity.createIntent(this));
    }
}
