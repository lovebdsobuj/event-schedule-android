package com.xebia.xebicon2014.list;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.xebia.xebicon2014.R;
import com.xebia.xebicon2014.XebiConApp;
import com.xebia.xebicon2014.model.Talk;

/**
 * An ArrayAdapter to handle a list of Talks.
 */
public class TalkListAdapter extends ArrayAdapter<Talk> {
    int color;

    public TalkListAdapter(Context context) {

        super(context, 0);
        String color = ((XebiConApp) getContext().getApplicationContext()).getDataStore().getEvent().getBaseColor();
        this.color = Color.parseColor(color);

    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        if (view == null) {
            view = View.inflate(getContext(), R.layout.list_item_talk, null);
        }

        final Talk talk = getItem(position);
        ((TalkListItemView) view).setTintColor(color);
        ((TalkListItemView) view).showTalk(talk);

        return view;
    }
}
