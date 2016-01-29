package com.xebia.eventschedule;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.parse.ParseAnalytics;
import com.xebia.eventschedule.details.TalkActivity;
import com.xebia.eventschedule.details.TalkDetailsFragment;
import com.xebia.eventschedule.eventdetails.EventDetailsFragment;
import com.xebia.eventschedule.legal.LegalFragment;
import com.xebia.eventschedule.list.TalkListClickListener;
import com.xebia.eventschedule.list.TalkListFragment;
import com.xebia.eventschedule.model.Favorites;
import com.xebia.eventschedule.model.Talk;
import com.xebia.eventschedule.util.BaseEventScheduleApp;
import com.xebia.eventschedule.util.CalligraphyActivity;
import com.xebia.eventschedule.util.FavoritesNotificationScheduler;
import com.xebia.eventschedule.util.LayoutUtils;

/**
 * The main activity shows the list of talks, and gives access to the supplementary activities of
 * the application.
 */
public class MainActivity extends CalligraphyActivity implements TalkListClickListener,
        NavigationView.OnNavigationItemSelectedListener {

    private static final String INST_ST_FILTER_MENU_ID = "FilterMenuId";
    private static final String INST_ST_FILTER_MENU_TAG = "FilterMenuTag";
    private static final int DRAWER_CLOSE_DELAY_MS = 350;

    private final Handler mDrawerActionHandler = new Handler();
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private TalkListFragment mTalkListFragment;
    private EventDetailsFragment mEventDetailsFragment;
    private LegalFragment mLegalFragment;
    private boolean mScheduleVisible;
    private int mFilterMenuSelectedId;
    private String mFilterMenuSelectedTag;
    private CompoundButton mNavNotificationsToggle;
    private NavigationView mNavigationView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ParseAnalytics.trackAppOpenedInBackground(getIntent());
        setContentView(R.layout.activity_main);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState != null) {
            mTalkListFragment = (TalkListFragment) getSupportFragmentManager().findFragmentById(R.id.schedule_container);
            mFilterMenuSelectedId = savedInstanceState.getInt(INST_ST_FILTER_MENU_ID);
            mFilterMenuSelectedTag = savedInstanceState.getString(INST_ST_FILTER_MENU_TAG);
        }

        mNavigationView = (NavigationView) findViewById(R.id.navigation);
        final View drawerHeader = mNavigationView.getHeaderView(0);
        mNavNotificationsToggle = (CompoundButton) drawerHeader.findViewById(R.id.nav_notifications_switch);
        mNavNotificationsToggle.setChecked(FavoritesNotificationScheduler.isNotificationsEnabled(this));
        mNavNotificationsToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FavoritesNotificationScheduler
                        .setNotificationsEnabled(MainActivity.this, mNavNotificationsToggle.isChecked());
            }
        });
        // Can't define in XML because of file ordering
        ViewCompat.setLabelFor(drawerHeader.findViewById(R.id.nav_notifications_label), mNavNotificationsToggle.getId());

        // listen for navigation events
        mNavigationView.setNavigationItemSelectedListener(this);

        // set up the hamburger icon to open and close the drawer
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open,
                R.string.drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        navigate(R.id.nav_schedule);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(INST_ST_FILTER_MENU_ID, mFilterMenuSelectedId);
        outState.putString(INST_ST_FILTER_MENU_TAG, mFilterMenuSelectedTag);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.support.v7.appcompat.R.id.home) {
            return mDrawerToggle.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Handles clicks on the navigation menu.
     */
    @Override
    public boolean onNavigationItemSelected(final MenuItem menuItem) {
        menuItem.setChecked(true);
        mDrawerLayout.closeDrawer(GravityCompat.START);
        mDrawerActionHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                navigate(menuItem.getItemId());
            }
        }, DRAWER_CLOSE_DELAY_MS);
        return true;
    }

    private void navigate(@IdRes final int itemId) {
        mNavigationView.setCheckedItem(itemId);
        switch (itemId) {
            case R.id.nav_schedule:
                showSchedule();
                break;
            case R.id.nav_event_info:
                showEventDetails();
                break;
            case R.id.nav_legal:
                showLegal();
                break;
            default:
                // ignore
                break;
        }
    }

    private void showSchedule() {
        if (LayoutUtils.isDualPane(this)) {
            findViewById(R.id.talk_details).setVisibility(View.VISIBLE);
        }
        if (null == mTalkListFragment) {
            final BaseEventScheduleApp app = (BaseEventScheduleApp) getApplicationContext();
            mTalkListFragment = TalkListFragment.newInstance(app.getParseEventId());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.schedule_container, mTalkListFragment, "talkList")
                    .addToBackStack("talkList")
                    .commit();
        }

        getSupportFragmentManager()
                .popBackStackImmediate("talkList", 0);
        mScheduleVisible = true;
    }

    private void showEventDetails() {
        if (LayoutUtils.isDualPane(this)) {
            findViewById(R.id.talk_details).setVisibility(View.GONE);
        }
        if (null == mEventDetailsFragment) {
            final BaseEventScheduleApp app = (BaseEventScheduleApp) getApplicationContext();
            mEventDetailsFragment = EventDetailsFragment.newInstance(app.getParseEventId());
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.schedule_container, mEventDetailsFragment, "eventDetails")
                .addToBackStack("eventDetails")
                .commit();
        mScheduleVisible = false;
    }

    private void showLegal() {
        if (LayoutUtils.isDualPane(this)) {
            findViewById(R.id.talk_details).setVisibility(View.GONE);
        }
        if (null == mLegalFragment) {
            final BaseEventScheduleApp app = (BaseEventScheduleApp) getApplicationContext();
            mLegalFragment = LegalFragment.newInstance(app.getParseEventId());
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.schedule_container, mLegalFragment, "legal")
                .addToBackStack("legal")
                .commit();
        mScheduleVisible = false;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
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

    @Override
    public boolean onTalkLongClick(Talk talk) {
        if (talk.isAlwaysFavorite() || LayoutUtils.isDualPane(this)) {
            return false;
        }
        final Favorites favorites = Favorites.get();
        if (favorites.contains(talk)) {
            favorites.remove(talk);
            Toast.makeText(this, R.string.favorite_removed, Toast.LENGTH_SHORT).show();
        } else {
            favorites.add(talk);
            Toast.makeText(this, R.string.favorite_added, Toast.LENGTH_SHORT).show();
        }
        favorites.save(this);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
        } else if (mScheduleVisible && mTalkListFragment.isFiltered()) {
            mTalkListFragment.setFilteringDisabled();
        } else if (!mScheduleVisible) {
            navigate(R.id.nav_schedule);
        } else {
            finish();
        }
    }
}
