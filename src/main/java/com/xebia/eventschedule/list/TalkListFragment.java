package com.xebia.eventschedule.list;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.xebia.eventschedule.model.Favorites;
import com.xebia.eventschedule.model.Talk;
import com.xebia.eventschedule.model.TalkComparator;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment that just contains a list of talks. If the "favoritesOnly" boolean argument is
 * included and set to true, then the list of talks will be filtered to only show those which have
 * been favorited, plus the ones marked as "alwaysFavorite", such as meals.
 */
public class TalkListFragment extends ListFragment implements Favorites.Listener {

    private static final String ARG_FAVORITES_ONLY = "favoritesOnly";
    private TalkListAdapter adapter = null;
    private Listener mListener;

    // Whether or not to show only favorites.
    private boolean favoritesOnly = false;
    private boolean destroyed = false;

    // talks that have been unfavorited without removing them from the list of favorites
    private List<Talk> mQuietlyUnfavorited = new ArrayList<Talk>();

    // currently selected item, for highlighting the selection in master-detail mode
    private TalkListItemView mSelectedView;

    public static TalkListFragment newInstance(boolean favoritesOnly) {
        TalkListFragment fragment = new TalkListFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_FAVORITES_ONLY, favoritesOnly);
        fragment.setArguments(args);
        return fragment;
    }

    public TalkListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        favoritesOnly = args.getBoolean(ARG_FAVORITES_ONLY, false);

        // Fetch the list of all talks from Parse or the query cache.
        Talk.findInBackground(new FindCallback<Talk>() {
            @Override
            public void done(List<Talk> talks, ParseException e) {

                if (destroyed) {
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

    private void onTalksLoaded(List<Talk> talks) {

        adapter = new TalkListAdapter(getActivity());

        // Add all of the talks to the adapter, skipping any that were not favorited,
        // if favoritesOnly is true.
        for (Talk talk : talks) {
            if (!favoritesOnly || talk.isAlwaysFavorite() || Favorites.get().contains(talk)) {
                adapter.add(talk);
            }
        }

        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Talk talk = adapter.getItem(position);

        if (null != mSelectedView && !v.equals(mSelectedView)) mSelectedView.setHighlighted(false);
        mSelectedView = (TalkListItemView) v;
        mSelectedView.setHighlighted(true);

        if (null != mListener) {
            mListener.onTalkClick(talk);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyed = true;
    }

    @Override
    public void onDestroyView() {
        Favorites.get().removeListener(this);
        destroyed = true;
        super.onDestroyView();
    }

    @Override
    public void onFavoriteAdded(Talk talk) {
        // If a new talk becomes favorited, automatically add it to this list.
        if (adapter != null) {
            // make sure we do not add recently unfavorited talks twice
            if (favoritesOnly && !mQuietlyUnfavorited.contains(talk)) {
                adapter.add(talk);
                adapter.sort(TalkComparator.get());
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onFavoriteRemoved(Talk talk) {
        if (adapter != null) {
            if (favoritesOnly) {
                // do not remove the talk from the list yet, but keep track of it
                mQuietlyUnfavorited.add(talk);
            } else {
                adapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * Removes any talks from the list that have not been favorited.
     */
    public void removeUnfavoritedItems() {
        for (Talk talk : mQuietlyUnfavorited) {
            adapter.remove(talk);
        }
        mQuietlyUnfavorited.clear();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (Listener) activity;
        } catch (Exception e) {
            throw new ClassCastException("Parent activity should implement TalkListFragment" +
                    ".Listener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public static interface Listener {
        public abstract void onTalkClick(Talk talk);
    }
}
