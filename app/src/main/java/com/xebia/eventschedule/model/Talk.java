package com.xebia.eventschedule.model;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQuery.CachePolicy;
import com.xebia.eventschedule.BuildConfig;
import com.xebia.eventschedule.util.LocaleUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * A talk being given at Parse Developer Day.
 */
@ParseClassName("Talk")
public class Talk extends ParseObject {

    private static final String TAG = "Talk";
    private boolean mSelected;

    /**
     * Creates a query for talks with all the includes and cache policy set.
     */
    private static ParseQuery<Talk> createQuery() {
        ParseQuery<Talk> query = new ParseQuery<>(Talk.class);
        query.include("speakers");
        query.include("room");
        query.include("slot");
        query.include("event");
        query.setCachePolicy(CachePolicy.CACHE_THEN_NETWORK);
        return query;
    }

    /**
     * Gets the objectId of the Talk associated with the given URI.
     */
    public static String getTalkId(Uri uri) {
        List<String> path = uri.getPathSegments();
        if (path.size() != 2 || !"talk".equals(path.get(0))) {
            throw new RuntimeException("Invalid URI for talk: " + uri);
        }
        return path.get(1);
    }

    /**
     * Retrieves the set of all talks, ordered by time. Uses the cache if possible.
     */
    public static void findInBackground(String eventId, final FindCallback<Talk> callback) {
        ParseQuery<Talk> query = Talk.createQuery();
        ParseQuery innerQuery = new ParseQuery("Event");
        innerQuery.whereEqualTo("objectId", eventId);

        query.whereMatchesQuery("event", innerQuery);
        query.findInBackground(new TalkFindCallback() {
            @Override
            protected void doneOnce(List<Talk> objects, ParseException e) {
                if (objects != null) {
                    // Sort the talks by start time.
                    Collections.sort(objects, TalkComparator.get());
                }
                callback.done(objects, e);
            }
        });
    }

    /**
     * Gets the data for a single talk. We use this instead of calling fetch on a ParseObject so that
     * we can use query cache if possible.
     */
    public static void getInBackground(final String objectId, final GetCallback<Talk> callback) {
        ParseQuery<Talk> query = Talk.createQuery();
        query.whereEqualTo("objectId", objectId);
        query.findInBackground(new TalkFindCallback() {
            @Override
            protected void doneOnce(List<Talk> objects, ParseException e) {
                if (objects != null) {
                    // Emulate the behavior of getFirstInBackground by using only the first result.
                    if (objects.size() < 1) {
                        callback.done(null, new ParseException(ParseException.OBJECT_NOT_FOUND,
                            "No talk with id " + objectId + " was found."));
                    } else {
                        callback.done(objects.get(0), e);
                    }
                } else {
                    callback.done(null, e);
                }
            }
        });
    }

    /**
     * Returns a URI to use in Intents to represent this talk. The format is
     * parsedevday://talk/theObjectId
     */
    @NonNull
    public Uri getUri() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("parsedevday");
        builder.path("talk/" + getObjectId());
        return builder.build();
    }

    @NonNull
    public String getTitle() {
        Map<String, String> titleMap = getMap("title");
        String localizedTitle = titleMap.get(LocaleUtils.getLocaleLabel(getEvent()));
        if (null == localizedTitle) {
            localizedTitle = "";
        }
        return localizedTitle;
    }

    @NonNull
    public String getAbstract() {
        Map<String, String> abstractMap = getMap("abstract");
        String localizedAbstract = abstractMap.get(LocaleUtils.getLocaleLabel(getEvent()));
        if (null == localizedAbstract) {
            localizedAbstract = "";
        }
        return localizedAbstract;
    }

    @Nullable
    public List<Speaker> getSpeakers() {
        return getList("speakers");
    }

    @Nullable
    public Slot getSlot() {
        return (Slot) get("slot");
    }

    @Nullable
    public Room getRoom() {
        return (Room) get("room");
    }

    public boolean hasRoom() {
        return getRoom() != null;
    }

    @Nullable
    public String getRoomName() {
        if (null != getRoom()) {
            return getRoom().getName();
        }
        return null;
    }

    @NonNull
    public List<String> getTags() {
        List<String> results = new ArrayList<>();
        try {
            JSONObject tags = getJSONObject("tags");
            if (null != tags) {
                JSONArray localizedTags = tags.getJSONArray(LocaleUtils.getLocaleLabel(getEvent()));
                for (int i = 0; i < localizedTags.length(); i++) {
                    results.add(localizedTags.getString(i));
                }
            }
        } catch (JSONException e) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "Could not parse list of tags", e);
            }
        }
        return results;
    }

    @Nullable
    public Event getEvent() {
        return (Event) get("event");
    }

    /**
     * Items like breaks and keynotes are marked as "alwaysFavorite" so they always show up on
     * the Favorites tab of the schedule. We also color them slightly differently to make the UI
     * prettier.
     */
    public boolean isAlwaysFavorite() {
        return isBreak() || isKeynote();
    }

    public boolean isBreak() {
        return "break".equalsIgnoreCase(getString("type"))
            || "lunch".equalsIgnoreCase(getString("type"))
            || "registration".equalsIgnoreCase(getString("type"))
            || "drinks".equalsIgnoreCase(getString("type"));
    }

    public boolean isKeynote() {
        return "keynote".equalsIgnoreCase(getString("type"));
    }

    /**
     * Keeps track of the selected talk in the list of talks. Only useful in tablets with master/detail layout.
     *
     * @return <code>true</code> if the talk is currently selected
     */
    public boolean isSelected() {
        return mSelected;
    }

    /**
     * Marks the talk as currently selected in the list of talks. Only useful in tablets with master/detail layout.
     */
    public void setSelected(boolean selected) {
        mSelected = selected;
    }

    /**
     * Wraps a FindCallback so that we can use the CACHE_THEN_NETWORK caching policy, but only call
     * the callback once, with the first data available.
     */
    private abstract static class TalkFindCallback implements FindCallback<Talk> {
        private boolean isCachedResult = true;
        private boolean calledCallback = false;

        @Override
        public void done(List<Talk> objects, ParseException e) {
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
        protected abstract void doneOnce(List<Talk> objects, ParseException e);
    }
}
