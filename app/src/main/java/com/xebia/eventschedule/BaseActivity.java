package com.xebia.eventschedule;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public abstract class BaseActivity extends ActionBarActivity {

    private static final int DRAWER_CLOSE_DELAY_MS = 400;
    public static final int NAV_ITEM_TALKS = 0;
    public static final int NAV_ITEM_FAVORITES = 1;
    public static final int NAV_ITEM_EVENT_DETAILS = 2;
    public static final int NAV_ITEM_LEGAL = 3;

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private Handler mDrawerActionHandler;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDrawerActionHandler = new Handler();

        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    protected abstract int getSelfNavDrawerItem();

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupNavDrawer();
    }

    private void setupNavDrawer() {
        String[] navItems = getResources().getStringArray(R.array.nav_items);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ListView drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerList.setAdapter(new ArrayAdapter<>(this, R.layout.navdrawer_item,
                R.id.navdrawer_item_label, navItems));
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view,
                                    final int position, final long id) {
                if (position == getSelfNavDrawerItem()) {
                    mDrawerLayout.closeDrawer(Gravity.START);
                    return;
                }
                mDrawerLayout.closeDrawer(Gravity.START);
                mDrawerActionHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        navigate(position);
                    }
                }, DRAWER_CLOSE_DELAY_MS);
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open,
                R.string.drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

    private void navigate(final int position) {
        switch (position) {
            case NAV_ITEM_TALKS:
                // TODO: show talks fragment
                break;
            case NAV_ITEM_FAVORITES:
                // TODO: show favorites fragment
                break;
            case NAV_ITEM_EVENT_DETAILS:
                // TODO: show event details
                break;
            case NAV_ITEM_LEGAL:
                // TODO: show legal stuff
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
}
