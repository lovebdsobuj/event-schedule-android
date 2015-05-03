package com.xebia.eventschedule.model;

import java.util.Comparator;

/**
 * A Comparator that sorts talks first by time, then by room.
 */
public class TalkComparator implements Comparator<Talk> {
    /*
     * This is a Singleton, because it has no state, so it's silly to make new ones.
     */
    final private static TalkComparator instance = new TalkComparator();

    public static TalkComparator get() {
        return instance;
    }

    @Override
    public int compare(Talk lhs, Talk rhs) {
        int startTimeCompare = null != lhs.getSlot() && null != lhs.getSlot().getStartTime() && null != rhs.getSlot()
            ? lhs.getSlot().getStartTime().compareTo(rhs.getSlot().getStartTime())
            : 0;
        if (startTimeCompare != 0) {
            return startTimeCompare;
        } else if (null != lhs.getRoom() && null != lhs.getRoom().getName() && null != rhs.getRoom() && null != rhs.getRoom().getName()) {
            return lhs.getRoom().getName().compareTo(rhs.getRoom().getName());
        } else {
            return 0;
        }
    }
}
