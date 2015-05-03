package com.xebia.eventschedule.model;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Date;

/**
 * A time slot that a talk could be held in.
 */
@ParseClassName("Slot")
public class Slot extends ParseObject {

    @Nullable
    public Date getStartTime() {
        return getDate("startTime");
    }

    @Nullable
    public Date getEndTime() {
        return getDate("endTime");
    }

    /**
     * Returns a string representation of the time slot suitable for use in the UI.
     */
    @Nullable
    public String format(Context context) {
        java.text.DateFormat timeFormat = DateFormat.getTimeFormat(context);
        return null != getStartTime() && null != getEndTime()
            ? timeFormat.format(getStartTime()) + " - " + timeFormat.format(getEndTime())
            : null;
    }
}
