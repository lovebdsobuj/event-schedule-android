package com.xebia.xebicon2014.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.xebia.xebicon2014.model.Favorites;
import com.xebia.xebicon2014.model.Talk;
import com.xebia.xebicon2014.model.TalkComparator;

import java.util.List;

/**
 * A fragment that just contains a list of talks. If the "favoritesOnly" boolean argument is
 * included and set to true, then the list of talks will be filtered to only show those which have
 * been favorited, plus the ones marked as "alwaysFavorite", such as meals.
 */
public class TalkListFragment extends ListFragment implements Favorites.Listener {

    private static final String ARG_FAVORITES_ONLY = "favoritesOnly";
    private TalkListAdapter adapter = null;

    // Whether or not to show only favorites.
    private boolean favoritesOnly = false;

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
        favoritesOnly = args.getBoolean("favoritesOnly", false);

        adapter = new TalkListAdapter(getActivity());
        setListAdapter(adapter);

        // Fetch the list of all talks from Parse or the query cache.
        Talk.findInBackground(new FindCallback<Talk>() {
            @Override
            public void done(List<Talk> talks, ParseException e) {
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

                // Add all of the talks to the adapter, skipping any that were not favorited,
                // if favoritesOnly is true.
                for (Talk talk : talks) {
                    if (!favoritesOnly || talk.isAlwaysFavorite() || Favorites.get().contains
                            (talk)) {
                        adapter.add(talk);
                    }
                }
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Talk talk = adapter.getItem(position);
        Intent intent = new Intent(getActivity(), TalkActivity.class);
        intent.setData(talk.getUri());
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        Favorites.get().removeListener(this);
        super.onDestroyView();
    }

    @Override
    public void onFavoriteAdded(Talk talk) {
        // If a new talk becomes favorited, automatically add it to this list.
        if (adapter != null) {
            if (favoritesOnly) {
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
                // This is commented out because we do not want favorites to disappear immediately.
                // adapter.remove(talk);
            } else {
                adapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * Removes any talks from the list that have not been favorited, if favoritesOnly is true.
     */
    public void removeUnfavoritedItems() {
        if (!favoritesOnly) {
            return;
        }
        for (int i = 0; adapter != null && i < adapter.getCount(); ++i) {
            Talk talk = adapter.getItem(i);
            if (!talk.isAlwaysFavorite() && !Favorites.get().contains(talk)) {
                adapter.remove(talk);
                i--;
            }
        }
    }
}
