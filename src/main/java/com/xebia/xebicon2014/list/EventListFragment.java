package com.xebia.xebicon2014.list;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;
import com.xebia.xebicon2014.R;
import com.xebia.xebicon2014.details.EventListAdapter;
import com.xebia.xebicon2014.model.Event;

/**
 * A fragment that just contains a list of events.
 */
public class EventListFragment extends ListFragment {

    private ParseQueryAdapter<Event> adapter = null;
    private Listener mListener;

    public static EventListFragment newInstance() {
        EventListFragment fragment = new EventListFragment();
        return fragment;
    }

    public EventListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //adapter = new ParseQueryAdapter<Event>(getActivity(), "Event");
        //adapter.setTextKey("name");
        //adapter.setImageKey("logo");

        adapter = new EventListAdapter(getActivity(), "Event");
        //adapter.setTextKey("name");
        //adapter.setImageKey("logo");
        setListAdapter(adapter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.event_list, null);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Event event = adapter.getItem(position);

        if (null != mListener) {
           mListener.onEventClick((Event)event);
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (Listener) activity;
        } catch (Exception e) {
            throw new ClassCastException("Parent activity should implement EventListFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public static interface Listener {
        public abstract void onEventClick(Event event);
    }
}
