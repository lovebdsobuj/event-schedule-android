package com.xebia.eventschedule;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class NavListAdapter<T> extends ArrayAdapter<T> {

    private int mHighlight;

    public NavListAdapter(Context context, T[] objects) {
        super(context, R.layout.navdrawer_item, R.id.navdrawer_item_label, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        view.setBackgroundColor(position == mHighlight ? Color.parseColor("#1f000000")
                : Color.parseColor("#00000000"));
        return view;
    }

    public void setHighlight(int position) {
        mHighlight = position;
        notifyDataSetChanged();
    }
}
