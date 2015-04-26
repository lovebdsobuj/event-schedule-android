package com.xebia.eventschedule.model;


import android.util.Log;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Model for a conference event.
 *
 * Contains both information about the event itself (name, location) and about the event dataset (languages, colors).
 */
@ParseClassName("Event")
public class Event extends ParseObject implements Serializable {

    /**
     * Wraps a FindCallback so that we can use the CACHE_THEN_NETWORK caching policy, but only call
     * the callback once, with the first data available.
     */
    private abstract static class EventFindCallback implements FindCallback<Event> {
        private boolean isCachedResult = true;
        private boolean calledCallback = false;

        @Override
        public void done(List<Event> objects, ParseException e) {
            if (!calledCallback) {
                if (objects != null) {
                    // We got a result, use it.
                    calledCallback = true;
                    doneOnce(objects, null);
                } else if (!isCachedResult) {
                    // We got called back twice, but got a null result both times. Pass on the latest error.
                    doneOnce(null, e);
                }
            }
            isCachedResult = false;
        }

        /**
         * Override this method with the callback that should only be called once.
         */
        protected abstract void doneOnce(List<Event> objects, ParseException e);
    }

    /**
     * Creates a query for event with all the includes and cache policy set.
     */
    private static ParseQuery<Event> createQuery() {
        ParseQuery<Event> query = new ParseQuery<>(Event.class);
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        query.include("location");
        return query;
    }

    /**
     * Gets the data for a single event. We use this instead of calling fetch on a ParseObject so that
     * we can use query cache if possible.
     */
    public static void getInBackground(final String objectId, final GetCallback<Event> callback) {
        ParseQuery<Event> query = Event.createQuery();
        query.whereEqualTo("objectId", objectId);
        query.findInBackground(new EventFindCallback() {
            @Override
            protected void doneOnce(List<Event> objects, ParseException e) {
                if (objects != null) {
                    // Emulate the behavior of getFirstInBackground by using only the first result.
                    if (objects.size() < 1) {
                        callback.done(null, new ParseException(ParseException.OBJECT_NOT_FOUND,
                            "No event with id " + objectId + " was found."));
                    } else {
                        callback.done(objects.get(0), e);
                    }
                } else {
                    callback.done(null, e);
                }
            }
        });
    }

    public String getName() {
        return getString("name");
    }

    public Location getLocation() {
        return (Location) getParseObject("location");
    }

    public String getTintColor() {
        return getString("tintColor");
    }

    public String getBaseColor() {
        return getString("baseColor");
    }

    public String getId() {
        return getObjectId();
    }

    public List<String> getLanguages() {
        List<String> results = new ArrayList<>();
        try {
            JSONArray languages = getJSONArray("languages");
            if (null != languages) {
                for (int i = 0; i < languages.length(); i++) {
                    results.add(languages.getString(i));
                }
            }
        } catch (JSONException e) {
            Log.d("Event", "Could not parse list of languages", e);
        }
        return results;
    }
}
