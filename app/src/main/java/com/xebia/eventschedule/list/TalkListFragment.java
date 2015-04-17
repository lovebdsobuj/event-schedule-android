package com.xebia.eventschedule.list;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.xebia.eventschedule.R;
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
public class TalkListFragment extends Fragment implements Favorites.Listener {

    private static final String ARG_FAVORITES_ONLY = "favoritesOnly";
    private static final String ARG_EVENT_ID = "eventId";
    private TalkListAdapter mAdapter = null;
    private TalkListClickListener mListener;

    // Whether or not to show only favorites.
    private boolean favoritesOnly = false;
    private boolean destroyed = false;

    // talks that have been unfavorited without removing them from the list of favorites
    private List<Talk> mQuietlyUnfavorited = new ArrayList<>();

    public static TalkListFragment newInstance(String eventId, boolean favoritesOnly) {
        TalkListFragment fragment = new TalkListFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_FAVORITES_ONLY, favoritesOnly);
        args.putString(ARG_EVENT_ID, eventId);
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

        String eventId = getArguments().getString(ARG_EVENT_ID);
        Talk.findInBackground(eventId, new FindCallback<Talk>() {

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.talk_list, container, false);
        RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new TalkListAdapter(mListener);
        mRecyclerView.setAdapter(mAdapter);
        return rootView;
    }

    /**
     * Adds all of the talks to the adapter, skipping any that were not favorited, if favoritesOnly is true.
     *
     * @param talks List of loaded talks
     */
    private void onTalksLoaded(@NonNull List<Talk> talks) {
        for (Talk talk : talks) {
            if (!favoritesOnly || talk.isAlwaysFavorite() || Favorites.get().contains(talk)) {
                mAdapter.add(talk);
            }
        }
        mListener.onTalksLoaded(talks);
        mAdapter.notifyDataSetChanged();
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
        if (mAdapter != null) {
            // make sure we do not add recently unfavorited talks twice
            if (favoritesOnly && !mQuietlyUnfavorited.contains(talk)) {
                mAdapter.add(talk);
                mAdapter.sort(TalkComparator.get());
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onFavoriteRemoved(Talk talk) {
        if (mAdapter != null) {
            if (favoritesOnly) {
                // do not remove the talk from the list yet, but keep track of it
                mQuietlyUnfavorited.add(talk);
            } else {
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * Removes any talks from the list that have not been favorited.
     */
    public void removeUnfavoritedItems() {
        for (Talk talk : mQuietlyUnfavorited) {
            mAdapter.remove(talk);
        }
        mQuietlyUnfavorited.clear();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (TalkListClickListener) activity;
        } catch (Exception e) {
            throw new ClassCastException("Parent activity should implement TalkListClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
