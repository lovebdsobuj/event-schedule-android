package com.xebia.eventschedule.list;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.xebia.eventschedule.model.Talk;

public class TalkViewHolder extends RecyclerView.ViewHolder {

    private final ScheduleListItemView mItemView;

    public TalkViewHolder(final ScheduleListItemView itemView) {
        super((View) itemView);
        mItemView = itemView;
    }

    public void setTalk(final Talk talk) {
        mItemView.showTalk(talk);
    }
}
