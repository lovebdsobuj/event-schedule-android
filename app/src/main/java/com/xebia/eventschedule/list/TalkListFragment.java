package com.xebia.eventschedule.list;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.xebia.eventschedule.R;
import com.xebia.eventschedule.model.Favorites;
import com.xebia.eventschedule.model.Tags;
import com.xebia.eventschedule.model.Talk;
import com.xebia.eventschedule.util.AccentColorSpan;

import java.util.List;

import timber.log.Timber;

/**
 * A fragment that just contains a list of talks.
 */
public class TalkListFragment extends Fragment implements Favorites.Listener {

    private static final String ARG_EVENT_ID = "eventId";
    private TalkListAdapter mAdapter = null;
    private TalkListClickListener mListener;
    private View mLoadingIndicator;

    private MenuItem mFilterItemMenu;
    private SubMenu mFilterItemSubMenu;
    private int mFilterMenuSelectedId;
    private String mFilterMenuSelectedTag;

    private boolean mDestroyed = false;

    public static TalkListFragment newInstance(String eventId) {
        TalkListFragment fragment = new TalkListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();

        mDestroyed = false;
        Bundle args = getArguments();
        String eventId = args.getString(ARG_EVENT_ID);
        Talk.findInBackground(eventId, new FindCallback<Talk>() {

            @Override
            public void done(List<Talk> talks, ParseException e) {

                if (mDestroyed) {
                    // do not perform fragment transaction on destroyed activity
                    return;
                }
                // When the set of favorites changes, update this UI.
                Favorites.get().addListener(TalkListFragment.this);

                if (e != null) {
                    Toast toast = Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }

                if (talks == null) {
                    throw new RuntimeException("Somehow the list of talks was null.");
                }

                onTalksLoaded(talks);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.talk_list, container, false);
        mLoadingIndicator = rootView.findViewById(R.id.loading_indicator);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new TalkListAdapter(mListener);
        recyclerView.setAdapter(mAdapter);
        if (savedInstanceState != null) {
            mAdapter.onRestoreInstanceState(savedInstanceState);
        }
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.activity_main, menu);
        mFilterItemMenu = menu.findItem(R.id.menu_filter);
        mFilterItemSubMenu = mFilterItemMenu.getSubMenu();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getGroupId() == R.id.menu_filter_group) {
            mFilterMenuSelectedId = item.getItemId();
            mFilterMenuSelectedTag = null;
            item.setChecked(true);
            if (item.getItemId() == R.id.menu_filter_item_everything) {
                setFilteringDisabled();
            } else if (item.getItemId() == R.id.menu_filter_item_favourites) {
                setFilterFavourites();
            } else {
                final String chosenTag = String.valueOf(item.getTitle());
                setFilterByTag(chosenTag);
                mFilterMenuSelectedId = R.id.menu_filter_item_any_tag;
                mFilterMenuSelectedTag = chosenTag;
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean isFiltered() {
        return mFilterMenuSelectedId == R.id.menu_filter_item_favourites
                || mFilterMenuSelectedId == R.id.menu_filter_item_any_tag;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mAdapter.onSaveInstanceState(outState);
    }

    /**
     * Adds all of the talks to the adapter
     *
     * @param talks List of loaded talks
     */
    private void onTalksLoaded(@NonNull List<Talk> talks) {
        setLoadingIndicatorVisibility(false);
        mAdapter.clear();
        mAdapter.addAll(talks);
        initFilter(talks);
        mAdapter.notifyDataSetChanged();
    }

    private void initFilter(@NonNull List<Talk> talks) {

        if (mFilterItemSubMenu == null) {
            // TODO aargh! We can NPE here when you rotate the screen. That's really awkward and we should fix it.
            Timber.w("onTalksLoaded() called before onCreateOptionsMenu()");
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

            final List<String> tagsOrdered = Tags.init(getContext(), talks).getTagLabels();

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

            final Tags tags = Tags.init(getContext(), talks);
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
    public void onDestroy() {
        super.onDestroy();
        mDestroyed = true;
    }

    @Override
    public void onDestroyView() {
        Favorites.get().removeListener(this);
        mDestroyed = true;
        super.onDestroyView();
    }

    @Override
    public void onFavoriteAdded(final Talk talk) {
        if (mAdapter != null) {
            mAdapter.onTalkUpdated(talk);
        }
    }

    @Override
    public void onFavoriteRemoved(final Talk talk) {
        if (mAdapter != null) {
            mAdapter.onTalkUpdated(talk);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (TalkListClickListener) context;
        } catch (Exception e) {
            throw new ClassCastException("Context should implement TalkListClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setFilterByTag(@NonNull String tag) {
        if (mAdapter != null) {
            mAdapter.setFilterByTag(tag);
            mAdapter.notifyDataSetChanged();
        }
    }

    public void setFilterFavourites() {
        if (mAdapter != null) {
            mAdapter.setFilterFavourites();
            mAdapter.notifyDataSetChanged();
        }
    }

    public void setFilteringDisabled() {
        mFilterMenuSelectedId = R.id.menu_filter_item_everything;
        mFilterMenuSelectedTag = null;
        if (mAdapter != null) {
            mAdapter.setFilteringDisabled();
            mAdapter.notifyDataSetChanged();
        }
    }

    private void setLoadingIndicatorVisibility(boolean visible) {
        mLoadingIndicator.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}
