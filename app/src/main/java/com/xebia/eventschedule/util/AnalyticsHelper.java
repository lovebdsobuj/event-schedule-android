package com.xebia.eventschedule.util;

import android.support.v4.util.ArrayMap;

import com.parse.ParseAnalytics;
import com.xebia.eventschedule.model.Favorites;
import com.xebia.eventschedule.model.Talk;

import java.util.Map;

/**
 * Helper for sending analytics events to Parse. Takes care of creating the event data with the right key/value pairs.
 */
public class AnalyticsHelper implements Favorites.Listener {

    private static final String KEY_EVENT_ID = "eventId";
    private static final String KEY_TALK_ID = "talkId";
    private static final String KEY_ACTION = "action";
    private static final String KEY_NOTIFICATIONS_ENABLED = "notificationsEnabled";

    private final String mEventId;

    public AnalyticsHelper(final String eventId) {
        mEventId = eventId;
    }

    public static void notification(final String eventId, final String talkId, final boolean notificationsEnabled) {
        final Map<String, String> dimens = new ArrayMap<>(3);
        dimens.put(KEY_EVENT_ID, eventId);
        dimens.put(KEY_TALK_ID, talkId);
        dimens.put(KEY_NOTIFICATIONS_ENABLED, Boolean.toString(notificationsEnabled));
        ParseAnalytics.trackEventInBackground("FavTalkNotification", dimens);
    }

    public static void toggleFavorite(final String eventId, final String talkId, final String action) {
        final Map<String, String> dimens = new ArrayMap<>(3);
        dimens.put(KEY_EVENT_ID, eventId);
        dimens.put(KEY_TALK_ID, talkId);
        dimens.put(KEY_ACTION, action);
        ParseAnalytics.trackEventInBackground("ToggleFavourite", dimens);
    }

    public static void openedTalkDetailsActivity(final String eventId, final String talkId) {
        final Map<String, String> dimens = new ArrayMap<>(2);
        dimens.put(KEY_EVENT_ID, eventId);
        dimens.put(KEY_TALK_ID, talkId);
        ParseAnalytics.trackEventInBackground("OpenedTalkActivity", dimens);
    }

    public static void openedEventDetailsActivity(final String eventId) {
        final Map<String, String> dimens = new ArrayMap<>(1);
        dimens.put(KEY_EVENT_ID, eventId);
        ParseAnalytics.trackEventInBackground("OpenedEventDetailsActivity", dimens);
    }

    public static void openedLegalActivity(final String eventId) {
        final Map<String, String> dimens = new ArrayMap<>(1);
        dimens.put(KEY_EVENT_ID, eventId);
        ParseAnalytics.trackEventInBackground("OpenedLegalActivity", dimens);
    }

    @Override
    public void onFavoriteAdded(final Talk talk) {
        toggleFavorite(mEventId, talk.getObjectId(), "add");
    }

    @Override
    public void onFavoriteRemoved(final Talk talk) {
        toggleFavorite(mEventId, talk.getObjectId(), "remove");
    }
}
