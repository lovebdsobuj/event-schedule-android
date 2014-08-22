package com.xebia.xebicon2014.util;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.xebia.xebicon2014.model.Event;

import com.parse.ParseException;

import java.util.List;
import java.util.Set;

public class DataStore {

    Event selectedEvent;
    private static String TAG = "DataStore";

    public DataStore() {
        initChosenEvent();
    }


    private void initChosenEvent() {
        //check from the local datastore whether an event is saved
        ParseQuery<Event> query = ParseQuery.getQuery("Event");
        query.fromLocalDatastore();
        try {
            List<Event> events = query.find();
            if (events.size() > 0) {
                selectedEvent = events.get(0);
                Log.d(TAG, "event loaded");
            } else {
                Log.d(TAG, "no event found");
            }

        } catch (ParseException e) {
            if (e.getMessage() != null) {
                Log.e(TAG, e.getMessage());
            }
        }

    }


    public void setNewSelectedEvent(Event newEvent) {
        selectedEvent = newEvent;
        newEvent.pinInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d(TAG, "eventSaved");
                } else {
                    if (e.getMessage() != null) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }
        });
    }

    public boolean isEventSelected() {
        return selectedEvent != null;
    }

    public Event getEvent() {
        return selectedEvent;
    }
}
