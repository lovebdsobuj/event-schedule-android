package com.xebia.eventschedule.list;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.xebia.eventschedule.R;
import com.xebia.eventschedule.model.Talk;

/**
 * An ArrayAdapter to handle a list of Talks.
 */
public class TalkListAdapter extends ArrayAdapter<Talk> {

    public TalkListAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        if (view == null) {
            view = View.inflate(getContext(), R.layout.list_item_talk, null);
        }

        final Talk talk = getItem(position);
        ((TalkListItemView) view).showTalk(talk);

        return view;
    }
}
