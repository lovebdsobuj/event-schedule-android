package com.xebia.xebicon2014.model;


import android.net.Uri;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.xebia.xebicon2014.util.LocaleUtils;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/*
 * the Event class in which talks are grouped
 */
@ParseClassName("Event")
public class Event extends ParseObject implements Serializable {

    /**
     * Wraps a FindCallback so that we can use the CACHE_THEN_NETWORK caching policy, but only call
     * the callback once, with the first data available.
     */
    private abstract static class EventFindCallback extends FindCallback<Event> {
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
     * Creates a query for talks with all the includes and cache policy set.
     */
    private static ParseQuery<Event> createQuery() {
        ParseQuery<Event> query = new ParseQuery<Event>(Event.class);
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        return query;
    }

    /**
     * Returns a URI to use in Intents to represent this talk. The format is
     * parsedevday://talk/theObjectId
     */
    public Uri getUri() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("parsedevday");
        builder.path("event/" + getObjectId());
        return builder.build();
    }

    /**
     * Retrieves the set of all talks, ordered by time. Uses the cache if possible.
     */
    public static void findInBackground(final FindCallback<Event> callback) {
        ParseQuery<Event> query = Event.createQuery();
        query.findInBackground(new EventFindCallback() {
            @Override
            protected void doneOnce(List<Event> objects, ParseException e) {
                if (objects != null) {
                    // Sort the events alphabetically.
                    Collections.sort(objects, new Comparator<Event>() {
                        @Override
                        public int compare(Event event1, Event event2) {
                            return event1.getDate("startDate").compareTo(event2.getDate("startDate"));
                        }
                    });
                }
                callback.done(objects, e);
            }
        });
    }

    /**
     * Gets the data for a single talk. We use this instead of calling fetch on a ParseObject so that
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

    public String getLocation() {
        return getString("location");
    }

    public String getTintColor(){
        return getString("tintColor");
    }

    public String getBaseColor(){
        return getString("baseColor");
    }


    public String getId(){
        return getObjectId();
    }

}
