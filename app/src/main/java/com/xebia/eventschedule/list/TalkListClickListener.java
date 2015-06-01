package com.xebia.eventschedule.list;

import android.support.annotation.NonNull;

import com.xebia.eventschedule.model.Talk;

import java.util.List;

/**
 * Listener for click events on the talk list items
 */
public interface TalkListClickListener {
    void onTalksLoaded(@NonNull List<Talk> talks);
    void onTalkClick(Talk talk);
    boolean onTalkLongClick(Talk talk);
}
