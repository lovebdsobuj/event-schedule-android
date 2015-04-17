package com.xebia.eventschedule.list;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;

import com.xebia.eventschedule.model.Talk;

import java.util.ArrayList;
import java.util.List;

/**
 * An ArrayAdapter to handle a list of Talks.
 */
public class TalkListAdapter extends RecyclerView.Adapter<TalkViewHolder> {

    private static final int BREAK_TYPE = 1;
    private static final int TALK_TYPE = 2;
    private static final String TAG = "TalkListAdapter";
    private static final String INST_ST_FILTER_MODE = "FilterMode";
    private static final String INST_ST_FILTER_TAG = "FilterTag";
    private final List<Talk> mAllData;
    private final List<Talk> mFilteredData;
    private final TalkListClickListener mListener;
    private FilterMode mFilterMode = FilterMode.NO_FILTERING;
    private String mFilterTag;

    public TalkListAdapter(final TalkListClickListener listener) {
        mListener = listener;
        mAllData = new ArrayList<>();
        mFilteredData = new ArrayList<>();
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
        holder.setTalk(mFilteredData.get(position));
    }

    @Override
    public int getItemViewType(final int position) {
        Talk talk = mFilteredData.get(position);
        return null != talk && talk.isBreak() ? BREAK_TYPE : TALK_TYPE;
    }

    @Override
    public int getItemCount() {
        return mFilteredData.size();
    }

    /**
     * After invoking this method, you must manually notify dataset changed to the RecyclerView.
     */
    public void clear() {
        mAllData.clear();
        mFilteredData.clear();
    }

    /**
     * After invoking this method, you must manually notify dataset changed to the RecyclerView.
     */
    public void addAll(final List<Talk> talks) {
        mAllData.addAll(talks);
        updateFilteredData();
    }

    /**
     * After invoking this method, you must manually notify dataset changed to the RecyclerView.
     */
    public void setFilterByTag(@NonNull final String tag) {
        this.mFilterMode = FilterMode.BY_TAG;
        this.mFilterTag = tag;
        updateFilteredData();
    }

    /**
     * After invoking this method, you must manually notify dataset changed to the RecyclerView.
     */
    public void setFilterFavourites() {
        this.mFilterMode = FilterMode.FAVOURITES;
        this.mFilterTag = null;
        updateFilteredData();
    }

    /**
     * After invoking this method, you must manually notify dataset changed to the RecyclerView.
     */
    public void setFilteringDisabled() {
        this.mFilterMode = FilterMode.NO_FILTERING;
        this.mFilterTag = null;
        updateFilteredData();
    }

    private void updateFilteredData() {
        mFilteredData.clear();
        if (mFilterMode == FilterMode.BY_TAG) {
            for (Talk talk : mAllData) {
                if (talk.getTags().contains(mFilterTag)) {
                    mFilteredData.add(talk);
                }
            }
        } else if (mFilterMode == FilterMode.FAVOURITES) {
            for (Talk talk : mAllData) {
                if (talk.isAlwaysFavorite() || talk.isHighlighted()) {
                    mFilteredData.add(talk);
                }
            }
        } else {
            mFilteredData.addAll(mAllData);
        }
    }

    public void onSaveInstanceState(@NonNull final Bundle outState) {
        outState.putInt(INST_ST_FILTER_MODE, mFilterMode.ordinal());
        outState.putString(INST_ST_FILTER_TAG, mFilterTag);
    }

    public void onRestoreInstanceState(@NonNull final Bundle inState) {
        mFilterMode = FilterMode.values()[inState.getInt(INST_ST_FILTER_MODE)];
        mFilterTag = inState.getString(INST_ST_FILTER_TAG);
    }

    private enum FilterMode { NO_FILTERING, FAVOURITES, BY_TAG }
}
