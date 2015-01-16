package com.xebia.eventschedule.util;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.PushService;
import com.xebia.eventschedule.BuildConfig;
import com.xebia.eventschedule.MainActivity;
import com.xebia.eventschedule.model.Event;
import com.xebia.eventschedule.model.Favorites;
import com.xebia.eventschedule.model.Room;
import com.xebia.eventschedule.model.Slot;
import com.xebia.eventschedule.model.Speaker;
import com.xebia.eventschedule.model.Talk;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * The base application class mostly handles setting up global configuration. This abstract class has to be extended
 * by a class called {@code EventScheduleApplication} in each different product flavor implementation.
 */
public abstract class BaseEventScheduleApp extends Application {

    @Override
    public void onCreate() {
        initParse();
        initCalligraphy();

        if (!BuildConfig.DEBUG) Parse.setLogLevel(Parse.LOG_LEVEL_NONE);
    }

    private void initCalligraphy() {
        CalligraphyConfig.initDefault("fonts/FuturaStd-Book.otf");
    }

    private void initParse() {
        // Register subclasses for various kinds of Parse Objects.
        ParseObject.registerSubclass(Room.class);
        ParseObject.registerSubclass(Slot.class);
        ParseObject.registerSubclass(Speaker.class);
        ParseObject.registerSubclass(Talk.class);
        ParseObject.registerSubclass(Event.class);

        // Initialize Parse with the application ID and client key.
        Parse.initialize(this, getParseApplicationId(), getParseClientKey());

        // Enable the Parse push notification service for remote pushes.
        PushService.setDefaultPushCallback(this, MainActivity.class);

        // Setup the listener to handle local notifications for when talks start.
        Favorites.get().addListener(new FavoritesNotificationScheduler(this));

        // Read in the favorites from the local disk on this device.
        Favorites.get().findLocally(this);
    }

    protected abstract String getParseClientKey();

    protected abstract String getParseApplicationId();

    public abstract String getParseEventId();
}
