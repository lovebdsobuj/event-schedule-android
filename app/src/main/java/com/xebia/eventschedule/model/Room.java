package com.xebia.eventschedule.model;

import android.support.annotation.Nullable;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * A room that a talk could be held in.
 */
@ParseClassName("Room")
public class Room extends ParseObject {

    @Nullable
    public String getName() {
        return getString("name");
    }
}
