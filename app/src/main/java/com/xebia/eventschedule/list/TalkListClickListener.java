package com.xebia.eventschedule.list;

import com.xebia.eventschedule.model.Talk;

/**
 * Listener for click events on the talk list items
 */
public interface TalkListClickListener {
    void onTalkClick(Talk talk);
    boolean onTalkLongClick(Talk talk);
}
