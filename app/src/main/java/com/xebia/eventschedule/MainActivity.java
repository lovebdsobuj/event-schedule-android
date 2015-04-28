package com.xebia.eventschedule;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.xebia.eventschedule.details.TalkActivity;
import com.xebia.eventschedule.details.TalkDetailsFragment;
import com.xebia.eventschedule.eventdetails.EventDetailsActivity;
import com.xebia.eventschedule.legal.LegalActivity;
import com.xebia.eventschedule.list.TalkListClickListener;
import com.xebia.eventschedule.list.TalkListFragment;
import com.xebia.eventschedule.model.Tags;
import com.xebia.eventschedule.model.Talk;
import com.xebia.eventschedule.util.BaseEventScheduleApp;
import com.xebia.eventschedule.util.CalligraphyActivity;
import com.xebia.eventschedule.util.LayoutUtils;

import java.util.List;

/**
 * The main activity shows the list of talks, and gives access to the supplementary activities of
 * the application.
 */
public class MainActivity extends CalligraphyActivity implements TalkListClickListener {
    private static final String INST_ST_FILTER_MENU_ID = "FilterMenuId";
    private static final String INST_ST_FILTER_MENU_TAG = "FilterMenuTag";
    private static final int DRAWER_CLOSE_DELAY_MS = 250;
    public static final int NAV_ITEM_SCHEDULE = 0;
    public static final int NAV_ITEM_EVENT_DETAILS = 1;
    public static final int NAV_ITEM_LEGAL = 2;

    private ActionBarDrawerToggle mDrawerToggle;
    private TalkListFragment mTalkListFragment;
    private DrawerLayout mDrawerLayout;
    private Handler mDrawerActionHandler;
    private int mNavPosition = NAV_ITEM_SCHEDULE;
    private NavListAdapter<String> mNavListAdapter;
    private Toolbar mToolbar;
    private MenuItem mFilterItemMenu;
    private SubMenu mFilterItemSubMenu;
    private int mFilterMenuSelectedId;
    private String mFilterMenuSelectedTag;
    private View mLoadingIndicator;

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

        if (savedInstanceState == null) {
            final BaseEventScheduleApp app = (BaseEventScheduleApp) getApplicationContext();
            mTalkListFragment = TalkListFragment.newInstance(app.getParseEventId());
            getSupportFragmentManager().beginTransaction().add(R.id.schedule_container, mTalkListFragment).commit();
        } else {
            mTalkListFragment = (TalkListFragment) getSupportFragmentManager().findFragmentById(R.id.schedule_container);
            mFilterMenuSelectedId = savedInstanceState.getInt(INST_ST_FILTER_MENU_ID);
            mFilterMenuSelectedTag = savedInstanceState.getString(INST_ST_FILTER_MENU_TAG);
        }
        navigate(NAV_ITEM_SCHEDULE);

        mLoadingIndicator = findViewById(R.id.loading_indicator);
        setLoadingIndicatorVisibility(true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(INST_ST_FILTER_MENU_ID, mFilterMenuSelectedId);
        outState.putString(INST_ST_FILTER_MENU_TAG, mFilterMenuSelectedTag);
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
                // nothing to do
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        mFilterItemMenu = menu.findItem(R.id.menu_filter);
        mFilterItemSubMenu = mFilterItemMenu.getSubMenu();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.support.v7.appcompat.R.id.home) {
            return mDrawerToggle.onOptionsItemSelected(item);
        }
        if (item.getGroupId() == R.id.menu_filter_group) {
            mFilterMenuSelectedId = item.getItemId();
            mFilterMenuSelectedTag = null;
            item.setChecked(true);
            if (item.getItemId() == R.id.menu_filter_item_everything) {
                mTalkListFragment.setFilteringDisabled();
            } else if (item.getItemId() == R.id.menu_filter_item_favourites) {
                mTalkListFragment.setFilterFavourites();
            } else {
                final String chosenTag = String.valueOf(item.getTitle());
                mTalkListFragment.setFilterByTag(chosenTag);
                mFilterMenuSelectedId = R.id.menu_filter_item_any_tag;
                mFilterMenuSelectedTag = chosenTag;
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTalksLoaded(@NonNull List<Talk> talks) {
        setLoadingIndicatorVisibility(false);

        if (mFilterItemSubMenu == null) {
            // TODO aargh! We can NPE here when you rotate the screen. That's really awkward and we should fix it.
            if (BuildConfig.DEBUG) {
                Log.w("MainActivity", "onTalksLoaded() called before onCreateOptionsMenu()");
            }
            return;
        }
        mFilterItemSubMenu.clear();
        if (talks.isEmpty()) {
            mFilterItemMenu.setVisible(false);
            mFilterMenuSelectedId = 0;
            mFilterMenuSelectedTag = null;
        } else {
            List<String> tagsOrdered = Tags.init(this, talks).getTagLabels();
            final MenuItem selectAll = mFilterItemSubMenu.add(R.id.menu_filter_group,
                    R.id.menu_filter_item_everything, 0, R.string.menu_filter_everything);
            selectAll.setChecked(mFilterMenuSelectedId == R.id.menu_filter_item_everything
                    || mFilterMenuSelectedId == 0);
            final MenuItem selectFavs = mFilterItemSubMenu.add(R.id.menu_filter_group,
                    R.id.menu_filter_item_favourites, 0, R.string.menu_filter_favourites);
            selectFavs.setChecked(mFilterMenuSelectedId == R.id.menu_filter_item_favourites);
            for (String title : tagsOrdered) {
                final MenuItem item = mFilterItemSubMenu.add(R.id.menu_filter_group, 0, 0, title);
                item.setChecked(mFilterMenuSelectedId == R.id.menu_filter_item_any_tag
                        && mFilterMenuSelectedTag != null && mFilterMenuSelectedTag.equals(title));
            }
            mFilterItemSubMenu.setGroupCheckable(R.id.menu_filter_group, true, true);
            mFilterItemMenu.setVisible(true);
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

    private void setLoadingIndicatorVisibility(boolean visible) {
        mLoadingIndicator.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}
