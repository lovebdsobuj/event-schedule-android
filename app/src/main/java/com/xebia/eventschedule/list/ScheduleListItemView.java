package com.xebia.eventschedule.list;

import com.xebia.eventschedule.model.Talk;

public interface ScheduleListItemView {

    void showTalk(Talk talk);

    void setHighlighted(boolean highlight);
}
