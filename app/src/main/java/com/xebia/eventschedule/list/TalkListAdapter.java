package com.xebia.eventschedule.list;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;

import com.xebia.eventschedule.model.Talk;
import com.xebia.eventschedule.model.TalkComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An ArrayAdapter to handle a list of Talks.
 */
public class TalkListAdapter extends RecyclerView.Adapter<TalkViewHolder> {

    private static final int BREAK_TYPE = 1;
    private static final int TALK_TYPE = 2;
    private static final String TAG = "TalkListAdapter";
    private final List<Talk> mData;
    private final TalkListClickListener mListener;

    public TalkListAdapter(final TalkListClickListener listener) {
        mListener = listener;
        mData = new ArrayList<>();
    }

    @Override
    public TalkViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        switch (viewType) {
            case BREAK_TYPE:
                BreakListItemView breakView = new BreakListItemView(parent.getContext(), mListener);
                return new TalkViewHolder(breakView);
            case TALK_TYPE:
                TalkListItemView talkView = new TalkListItemView(parent.getContext(), mListener);
                return new TalkViewHolder(talkView);
            default:
                Log.d(TAG, "Could not create view holder");
                return null;
        }
    }

    @Override
    public void onBindViewHolder(final TalkViewHolder holder, final int position) {
        holder.setTalk(mData.get(position));
    }

    @Override
    public int getItemViewType(final int position) {
        Talk talk = mData.get(position);
        return null != talk && talk.isBreak() ? BREAK_TYPE : TALK_TYPE;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void add(final Talk talk) {
        mData.add(talk);
    }

    public void sort(final TalkComparator talkComparator) {
        Collections.sort(mData, talkComparator);
    }

    public void remove(final Talk talk) {
        mData.remove(talk);
    }
}
