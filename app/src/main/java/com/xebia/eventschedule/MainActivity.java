package com.xebia.eventschedule;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.parse.ParseAnalytics;
import com.xebia.eventschedule.details.TalkActivity;
import com.xebia.eventschedule.details.TalkDetailsFragment;
import com.xebia.eventschedule.eventdetails.EventDetailsActivity;
import com.xebia.eventschedule.legal.LegalActivity;
import com.xebia.eventschedule.list.TalkListClickListener;
import com.xebia.eventschedule.list.TalkListFragment;
import com.xebia.eventschedule.model.Favorites;
import com.xebia.eventschedule.model.Tags;
import com.xebia.eventschedule.model.Talk;
import com.xebia.eventschedule.util.AccentColorSpan;
import com.xebia.eventschedule.util.BaseEventScheduleApp;
import com.xebia.eventschedule.util.CalligraphyActivity;
import com.xebia.eventschedule.util.FavoritesNotificationScheduler;
import com.xebia.eventschedule.util.LayoutUtils;

import java.util.List;

/**
 * The main activity shows the list of talks, and gives access to the supplementary activities of
 * the application.
 */
public class MainActivity extends CalligraphyActivity implements TalkListClickListener,
        NavigationView.OnNavigationItemSelectedListener {

    private static final String INST_ST_FILTER_MENU_ID = "FilterMenuId";
    private static final String INST_ST_FILTER_MENU_TAG = "FilterMenuTag";
    private static final int DRAWER_CLOSE_DELAY_MS = 250;

    private ActionBarDrawerToggle mDrawerToggle;
    private TalkListFragment mTalkListFragment;
    private DrawerLayout mDrawerLayout;
    private Handler mDrawerActionHandler;
    private Toolbar mToolbar;
    private MenuItem mFilterItemMenu;
    private SubMenu mFilterItemSubMenu;
    private int mFilterMenuSelectedId;
    private String mFilterMenuSelectedTag;
    private View mLoadingIndicator;
    private CompoundButton mNavNotificationsToggle;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ParseAnalytics.trackAppOpenedInBackground(getIntent());
        setContentView(R.layout.activity_main);
        mDrawerActionHandler = new Handler();
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        if (savedInstanceState == null) {
            final BaseEventScheduleApp app = (BaseEventScheduleApp) getApplicationContext();
            mTalkListFragment = TalkListFragment.newInstance(app.getParseEventId());
            getSupportFragmentManager().beginTransaction().add(R.id.schedule_container, mTalkListFragment).commit();
        } else {
            mTalkListFragment = (TalkListFragment) getSupportFragmentManager().findFragmentById(R.id.schedule_container);
            mFilterMenuSelectedId = savedInstanceState.getInt(INST_ST_FILTER_MENU_ID);
            mFilterMenuSelectedTag = savedInstanceState.getString(INST_ST_FILTER_MENU_TAG);
        }
        mNavNotificationsToggle = (CompoundButton) findViewById(R.id.nav_notifications_switch);
        navigate(R.id.nav_schedule);

        // Can't define in XML because of file ordering
        ViewCompat.setLabelFor(findViewById(R.id.nav_notifications_label), mNavNotificationsToggle.getId());

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
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(this);
        mNavNotificationsToggle.setChecked(FavoritesNotificationScheduler.isNotificationsEnabled(this));
        mNavNotificationsToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FavoritesNotificationScheduler
                        .setNotificationsEnabled(MainActivity.this, mNavNotificationsToggle.isChecked());
            }
        });
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open,
                R.string.drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem menuItem) {
        mDrawerLayout.closeDrawer(GravityCompat.START);
        mDrawerActionHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                navigate(menuItem.getItemId());
            }
        }, DRAWER_CLOSE_DELAY_MS);
        return true;
    }

    private void navigate(final int itemId) {
        switch (itemId) {
            case R.id.nav_schedule:
                // nothing to do
                break;
            case R.id.nav_event_info:
                startActivity(new Intent(this, EventDetailsActivity.class));
                break;
            case R.id.nav_legal:
                startActivity(new Intent(this, LegalActivity.class));
                break;
            default:
                // ignore
                break;
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
            final int colorBlipMarginRight = getResources().getDimensionPixelSize(R.dimen.menu_colorbadge_marginRight);
            final int colorBlipWidth = getResources().getDimensionPixelSize(R.dimen.menu_colorbadge_width);

            List<String> tagsOrdered = Tags.init(this, talks).getTagLabels();

            AccentColorSpan allColorBlip = new AccentColorSpan(Color.TRANSPARENT, colorBlipWidth, colorBlipMarginRight);
            final SpannableString allTitle = new SpannableString(getString(R.string.menu_filter_everything));
            allTitle.setSpan(allColorBlip, 0, allTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            final MenuItem selectAll = mFilterItemSubMenu
                    .add(R.id.menu_filter_group, R.id.menu_filter_item_everything, 0, allTitle);
            selectAll.setChecked(mFilterMenuSelectedId == R.id.menu_filter_item_everything
                    || mFilterMenuSelectedId == 0);

            AccentColorSpan favColorBlip = new AccentColorSpan(Color.TRANSPARENT, colorBlipWidth, colorBlipMarginRight);
            final SpannableString favTitle = new SpannableString(getString(R.string.menu_filter_favourites));
            favTitle.setSpan(favColorBlip, 0, favTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            final MenuItem selectFavs = mFilterItemSubMenu
                    .add(R.id.menu_filter_group, R.id.menu_filter_item_favourites, 0, favTitle);
            selectFavs.setChecked(mFilterMenuSelectedId == R.id.menu_filter_item_favourites);

            Tags tags = Tags.init(this, talks);
            for (String title : tagsOrdered) {
                final int color = tags.getTagColor(title);
                final AccentColorSpan colorBlip = new AccentColorSpan(color, colorBlipWidth, colorBlipMarginRight);
                final SpannableString menuTitle = new SpannableString(title);
                menuTitle.setSpan(colorBlip, 0, title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                final MenuItem item = mFilterItemSubMenu.add(R.id.menu_filter_group, 0, 0, menuTitle);
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
        } else if (mFilterMenuSelectedId == R.id.menu_filter_item_favourites
            || mFilterMenuSelectedId == R.id.menu_filter_item_any_tag) {
            onOptionsItemSelected(mFilterItemSubMenu.findItem(R.id.menu_filter_item_everything));
        } else {
            super.onBackPressed();
        }
    }

    private void setLoadingIndicatorVisibility(boolean visible) {
        mLoadingIndicator.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}
