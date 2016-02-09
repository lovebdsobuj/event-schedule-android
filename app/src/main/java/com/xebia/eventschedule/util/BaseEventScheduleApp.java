package com.xebia.eventschedule.util;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseCrashReporting;
import com.parse.ParseObject;
import com.xebia.eventschedule.BuildConfig;
import com.xebia.eventschedule.R;
import com.xebia.eventschedule.model.Event;
import com.xebia.eventschedule.model.Favorites;
import com.xebia.eventschedule.model.Location;
import com.xebia.eventschedule.model.Room;
import com.xebia.eventschedule.model.Slot;
import com.xebia.eventschedule.model.Speaker;
import com.xebia.eventschedule.model.Talk;

import timber.log.Timber;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * The base application class mostly handles setting up global configuration. This abstract class has to be extended
 * by a class called {@code EventScheduleApplication} in each different product flavor implementation.
 */
public abstract class BaseEventScheduleApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initLogging();
        initParse();
        initCalligraphy();

        if (!BuildConfig.DEBUG) Parse.setLogLevel(Parse.LOG_LEVEL_NONE);
    }

    private void initLogging() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    private void initCalligraphy() {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/FuturaStd-Book.otf")
                .setFontAttrId(R.attr.fontPath)
                .build());
    }

    private void initParse() {
        // Register subclasses for various kinds of Parse Objects.
        ParseObject.registerSubclass(Room.class);
        ParseObject.registerSubclass(Slot.class);
        ParseObject.registerSubclass(Speaker.class);
        ParseObject.registerSubclass(Talk.class);
        ParseObject.registerSubclass(Event.class);
        ParseObject.registerSubclass(Location.class);

        // Initialize Parse with the application ID and client key.
        ParseCrashReporting.enable(this);
        Parse.initialize(this, getParseApplicationId(), getParseClientKey());

        // Setup the listener to handle local notifications for when talks start.
        Favorites.get().addListener(new FavoritesNotificationScheduler(this));
        Favorites.get().addListener(new AnalyticsHelper(getParseEventId()));

        // Read in the favorites from the local disk on this device.
        Favorites.get().findLocally(this);
    }

    protected abstract String getParseClientKey();

    protected abstract String getParseApplicationId();

    public abstract String getParseEventId();
}
