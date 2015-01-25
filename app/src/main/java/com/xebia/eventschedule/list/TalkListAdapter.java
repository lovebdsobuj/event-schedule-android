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
        final Talk talk = getItem(position);

        if (view == null || (view instanceof TalkListItemView && talk.isBreak())
                || (view instanceof BreakListItemView && !talk.isBreak())) {
            view = View.inflate(getContext(), talk.isBreak() ? R.layout.list_item_break : R.layout.list_item_talk,
                    null);
        }

        if (view instanceof TalkListItemView) {
            ((TalkListItemView) view).showTalk(talk);
        } else if (view instanceof BreakListItemView) {
            ((BreakListItemView) view).showTalk(talk);
        }

        return view;
    }
}
