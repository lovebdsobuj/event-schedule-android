package com.xebia.eventschedule.list;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xebia.eventschedule.R;
import com.xebia.eventschedule.util.TypefaceSpan;

import java.util.Locale;

/**
 * View pager fragment that contains two talk list fragments: one list with all the talks,
 * one with just the user's favorites.
 * <p/>
 * Created by steven on 4-5-14.
 */
public class TalkListPagerFragment extends Fragment implements ActionBar.TabListener {
    private static final int TAB_SCHEDULE = 0;
    private static final int TAB_FAVORITES = 1;
    private static final int TAB_COUNT = 2;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.talk_list_pager, container, false);
        viewPager = (ViewPager) rootView.findViewById(R.id.pager);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // This adapter will return a fragment for each of the primary sections of the app.
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
        viewPager.setAdapter(sectionsPagerAdapter);

        // When swiping between different sections, select the corresponding tab.
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                getSupportActionBar().setSelectedNavigationItem(position);
            }
        });

        // Create the tabs for each section.
        for (int i = 0; i < sectionsPagerAdapter.getCount(); i++) {
            ActionBar.Tab tab = getSupportActionBar().newTab();
            CharSequence pageTitle = sectionsPagerAdapter.getPageTitle(i);
            SpannableString tabTitle = new SpannableString(pageTitle);
            String fontFamily = "FuturaStd-Book.otf";
            int textColor = getResources().getColor(R.color.tabs_text);
            tabTitle.setSpan(new TypefaceSpan(getActivity(), fontFamily, textColor), 0,
                    pageTitle.length(), 0);
            tab.setText(tabTitle);
            tab.setTabListener(this);
            getSupportActionBar().addTab(tab);
        }
    }

    private ActionBar getSupportActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        viewPager.setCurrentItem(tab.getPosition());

        // remove any items that have been unfavorited.
        SectionsPagerAdapter adapter = (SectionsPagerAdapter) viewPager.getAdapter();
        TalkListFragment fragment = (TalkListFragment) adapter.getItem(TAB_FAVORITES);
        fragment.removeUnfavoritedItems();
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // nothing to do
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // nothing to do
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the sections.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private TalkListFragment scheduleFragment = null;
        private TalkListFragment favoritesFragment = null;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            switch (position) {
                case TAB_SCHEDULE: {
                    if (scheduleFragment == null) {
                        scheduleFragment = TalkListFragment.newInstance(false);
                    }
                    return scheduleFragment;
                }
                case TAB_FAVORITES: {
                    if (favoritesFragment == null) {
                        favoritesFragment = TalkListFragment.newInstance(true);
                    }
                    return favoritesFragment;
                }
            }
            return null;
        }

        @Override
        public int getCount() {
            return TAB_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case TAB_SCHEDULE:
                    return getString(R.string.title_schedule).toUpperCase(l);
                case TAB_FAVORITES:
                    return getString(R.string.title_favorites).toUpperCase(l);
            }
            return null;
        }
    }
}
