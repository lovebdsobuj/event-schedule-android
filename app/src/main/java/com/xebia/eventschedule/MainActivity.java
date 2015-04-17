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
import com.xebia.eventschedule.list.TalkListFragment;
import com.xebia.eventschedule.model.Talk;
import com.xebia.eventschedule.util.BaseEventScheduleApp;
import com.xebia.eventschedule.util.CalligraphyActivity;
import com.xebia.eventschedule.util.LayoutUtils;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private MenuItem filterItemMenu;
    private SubMenu filterItemSubMenu;

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        filterItemMenu = menu.findItem(R.id.menu_filter);
        filterItemSubMenu = filterItemMenu.getSubMenu();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.support.v7.appcompat.R.id.home) {
            return mDrawerToggle.onOptionsItemSelected(item);
        }
        if (item.getGroupId() == R.id.menu_filter_group) {
            item.setChecked(true);
            if (item.getItemId() == R.id.menu_filter_item_everything) {
                // TODO tell the TalkListAdapter to show everything
            } else {
                final String chosenTag = String.valueOf(item.getTitle());
                // TODO tell the TalkListAdapter to show the given tag
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTalksLoaded(@NonNull List<Talk> talks) {
        filterItemSubMenu.clear();
        if (talks.isEmpty()) {
            filterItemMenu.setVisible(false);
        } else {
            List<String> tagsOrdered = getUniqueTalkTagsSorted(talks);
            if (tagsOrdered.isEmpty()) {
                return;
            }
            final MenuItem selectAll = filterItemSubMenu.add(R.id.menu_filter_group,
                    R.id.menu_filter_item_everything, 0, R.string.menu_filter_everything);
            selectAll.setChecked(true);
            for (String title : tagsOrdered) {
                filterItemSubMenu.add(R.id.menu_filter_group, 0, 0, title);
            }
            filterItemSubMenu.setGroupCheckable(R.id.menu_filter_group, true, true);
            filterItemMenu.setVisible(true);
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

    /**
     * Obtains the unique tags of the given Talks and returns them in lexical ordering by the default locale.
     *
     * @param talks the talks from which to obtain the tags.
     * @return the unique tags in lexical order. This list may be the immutable.
     */
    @NonNull
    private static List<String> getUniqueTalkTagsSorted(@NonNull List<Talk> talks) {
        final Set<String> tagsUnique = new HashSet<>();
        for (Talk talk : talks) {
            tagsUnique.addAll(talk.getTags());
        }
        if (tagsUnique.isEmpty()) {
            Log.d("TagFilterMenuBuilder", "There were no tags on any talk");
            return Collections.emptyList();
        }
        final List<String> tagsOrdered = new ArrayList<>(tagsUnique);
        Collections.sort(tagsOrdered, Collator.getInstance());
        return tagsOrdered;
    }
}
