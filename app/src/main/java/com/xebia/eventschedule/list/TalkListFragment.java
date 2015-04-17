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

import java.util.List;

/**
 * A fragment that just contains a list of talks.
 */
public class TalkListFragment extends Fragment implements Favorites.Listener {

    private static final String ARG_EVENT_ID = "eventId";
    private TalkListAdapter mAdapter = null;
    private TalkListClickListener mListener;
    private boolean destroyed = false;

    public static TalkListFragment newInstance(String eventId) {
        TalkListFragment fragment = new TalkListFragment();
        Bundle args = new Bundle();
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
        String eventId = args.getString(ARG_EVENT_ID);
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
        if (savedInstanceState != null) {
            mAdapter.onRestoreInstanceState(savedInstanceState);
        }
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mAdapter.onSaveInstanceState(outState);
    }

    /**
     * Adds all of the talks to the adapter, skipping any that were not favorited, if favoritesOnly is true.
     *
     * @param talks List of loaded talks
     */
    private void onTalksLoaded(@NonNull List<Talk> talks) {
        mAdapter.clear();
        mAdapter.addAll(talks);
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
        if (mAdapter != null) {
            mAdapter.setFilterFavourites();
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onFavoriteRemoved(Talk talk) {
        if (mAdapter != null) {
            mAdapter.setFilterFavourites();
            mAdapter.notifyDataSetChanged();
        }
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
        if (mAdapter != null) {
            mAdapter.setFilteringDisabled();
            mAdapter.notifyDataSetChanged();
        }
    }
}
