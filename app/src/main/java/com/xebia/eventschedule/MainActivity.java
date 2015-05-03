package com.xebia.eventschedule;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;

import com.xebia.eventschedule.details.TalkActivity;
import com.xebia.eventschedule.details.TalkDetailsFragment;
import com.xebia.eventschedule.eventdetails.EventDetailsActivity;
import com.xebia.eventschedule.legal.LegalActivity;
import com.xebia.eventschedule.list.TalkListClickListener;
import com.xebia.eventschedule.list.TalkListFragment;
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
public class MainActivity extends CalligraphyActivity implements TalkListClickListener {
    private static final String INST_ST_FILTER_MENU_ID = "FilterMenuId";
    private static final String INST_ST_FILTER_MENU_TAG = "FilterMenuTag";
    private static final int DRAWER_CLOSE_DELAY_MS = 250;

    private ActionBarDrawerToggle mDrawerToggle;
    private TalkListFragment mTalkListFragment;
    private DrawerLayout mDrawerLayout;
    private Handler mDrawerActionHandler;
    private int mNavPosition;
    private Toolbar mToolbar;
    private MenuItem mFilterItemMenu;
    private SubMenu mFilterItemSubMenu;
    private int mFilterMenuSelectedId;
    private String mFilterMenuSelectedTag;
    private View mLoadingIndicator;

    private View mNavSchedule;
    private View mNavEventInfo;
    private View mNavLegal;
    private CompoundButton mNavNotificationsToggle;

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
        mNavSchedule = findViewById(R.id.nav_schedule);
        mNavEventInfo = findViewById(R.id.nav_event_info);
        mNavLegal = findViewById(R.id.nav_legal);
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
        final View.OnClickListener navItemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                mDrawerLayout.closeDrawer(Gravity.START);
                mDrawerActionHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        navigate(v.getId());
                    }
                }, DRAWER_CLOSE_DELAY_MS);
            }
        };
        mNavSchedule.setOnClickListener(navItemClickListener);
        mNavEventInfo.setOnClickListener(navItemClickListener);
        mNavLegal.setOnClickListener(navItemClickListener);
        mNavNotificationsToggle.setChecked(FavoritesNotificationScheduler.isNotificationsEnabled(this));
        mNavNotificationsToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FavoritesNotificationScheduler.setNotificationsEnabled(MainActivity.this, mNavNotificationsToggle.isChecked());
            }
        });
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

    private void navigate(final int position) {
        if (position == mNavPosition) {
            return;
        }
        mNavPosition = position;
        mNavSchedule.setActivated(position == mNavSchedule.getId());
        mNavEventInfo.setActivated(position == mNavEventInfo.getId());
        mNavLegal.setActivated(position == mNavLegal.getId());
        switch (position) {
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

            for (String title : tagsOrdered) {
                final int color = Tags.get().getTagColor(title);
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
        } else if (mFilterMenuSelectedId == R.id.menu_filter_item_favourites
            || mFilterMenuSelectedId == R.id.menu_filter_item_any_tag) {
            onOptionsItemSelected(mFilterItemSubMenu.findItem(R.id.menu_filter_item_everything));
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigate(R.id.nav_schedule);
    }

    private void setLoadingIndicatorVisibility(boolean visible) {
        mLoadingIndicator.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}
