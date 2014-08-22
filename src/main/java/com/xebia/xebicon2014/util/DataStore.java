package com.xebia.xebicon2014.util;

import android.content.SharedPreferences;
import android.util.Log;


import com.xebia.xebicon2014.model.Event;

public class DataStore {

    private String selectedEventID;
    private static final String TAG = "DataStore";
    private static final String EVENT_ID = "event_id";
    private static final String NONE = "NONE";
    private SharedPreferences sharedPrefs;

    public DataStore(SharedPreferences preferences) {
        this.sharedPrefs = preferences;
    }


    private void initChosenEvent() {
        //check from the local datastore whether an event is saved
        selectedEventID = sharedPrefs.getString(EVENT_ID, NONE);
        if (selectedEventID != NONE) {
            Log.d(TAG, "eventID stored: " + selectedEventID);
        } else {
            Log.d(TAG, "no event stored");
        }
    }


    public void setNewSelectedEvent(Event newEvent) {
        selectedEventID = newEvent.getId();
        sharedPrefs.edit().putString(EVENT_ID, selectedEventID).apply();
    }

    public boolean isEventSelected() {
        return selectedEventID != null;
    }

    public String getEventId() {
        return selectedEventID;
    }
}
