package com.xebia.eventschedule;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.parse.ParseObject;
import com.xebia.eventschedule.details.TalkActivity;
import com.xebia.eventschedule.details.TalkDetailsFragment;
import com.xebia.eventschedule.eventdetails.EventDetailsActivity;
import com.xebia.eventschedule.legal.LegalActivity;
import com.xebia.eventschedule.list.TalkListFragment;
import com.xebia.eventschedule.model.Slot;
import com.xebia.eventschedule.model.Talk;
import com.xebia.eventschedule.util.BaseEventScheduleApp;
import com.xebia.eventschedule.util.CalligraphyActivity;
import com.xebia.eventschedule.util.FavoritesNotificationScheduler;
import com.xebia.eventschedule.util.LayoutUtils;

import java.util.Date;

/**
 * An Activity with a tabs for the complete schedule and the list of favorited talks. This was
 * originally created from an ADT wizard.
 */
public class MainActivity extends CalligraphyActivity implements TalkListFragment.Listener {

    private static final int DRAWER_CLOSE_DELAY_MS = 250;
    public static final int NAV_ITEM_SCHEDULE = 0;
    public static final int NAV_ITEM_FAVORITES = 1;
    public static final int NAV_ITEM_EVENT_DETAILS = 2;
    public static final int NAV_ITEM_LEGAL = 3;

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private Handler mDrawerActionHandler;
    private int mNavPosition = 0;
    private NavListAdapter<String> mNavListAdapter;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerActionHandler = new Handler();
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // Now retrieve the DrawerLayout so that we can set the status bar color.
        // This only takes effect on Lollipop, or when using translucentStatusBar
        // on KitKat.
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.primary));

        navigate(NAV_ITEM_SCHEDULE);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupNavDrawer();
    }

    private void setupNavDrawer() {
        String[] navItems = getResources().getStringArray(R.array.nav_items);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ListView drawerList = (ListView) findViewById(R.id.nav_list);
        mNavListAdapter = new NavListAdapter<>(this, navItems);
        drawerList.setAdapter(mNavListAdapter);
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view,
                                    final int position, final long id) {
                mDrawerLayout.closeDrawer(Gravity.START);
                if (position == mNavPosition) {
                    return;
                }
                mNavListAdapter.setHighlight(position);
                mDrawerActionHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        navigate(position);
                    }
                }, DRAWER_CLOSE_DELAY_MS);
            }
        });
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

    private void navigate(final int position) {
        switch (position) {
            case NAV_ITEM_SCHEDULE:
                Fragment sched = TalkListFragment.newInstance(((BaseEventScheduleApp) getApplicationContext())
                        .getParseEventId(), false);
                getSupportFragmentManager().beginTransaction().replace(R.id.schedule_container, sched, "sched").commit();
                mNavPosition = position;
                break;
            case NAV_ITEM_FAVORITES:
                Fragment favs = TalkListFragment.newInstance(((BaseEventScheduleApp) getApplicationContext())
                        .getParseEventId(), true);
                getSupportFragmentManager().beginTransaction().replace(R.id.schedule_container, favs, "favs").commit();
                mNavPosition = position;
                break;
            case NAV_ITEM_EVENT_DETAILS:
                startActivity(new Intent(this, EventDetailsActivity.class));
                break;
            case NAV_ITEM_LEGAL:
                startActivity(new Intent(this, LegalActivity.class));
                break;
            default:
                // ignore
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
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

    private boolean isNavDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(Gravity.START);
    }

    private void closeNavDrawer() {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(Gravity.START);
        }
    }

    @Override
    public void onBackPressed() {
        if (isNavDrawerOpen()) {
            closeNavDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNavListAdapter.setHighlight(mNavPosition);
    }
}
