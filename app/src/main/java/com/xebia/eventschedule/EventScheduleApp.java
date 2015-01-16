package com.xebia.eventschedule;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.PushService;
import com.xebia.eventschedule.model.Favorites;
import com.xebia.eventschedule.model.Room;
import com.xebia.eventschedule.model.Slot;
import com.xebia.eventschedule.model.Speaker;
import com.xebia.eventschedule.model.Talk;
import com.xebia.eventschedule.util.FavoritesNotificationScheduler;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * The main app class mostly handles setting up global state, such as Parse keys.
 */
public class EventScheduleApp extends Application {

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

        // Initialize Parse with the application ID and client key.
        Parse.initialize(this, "XqChc4Nc3GZOhsXOOHYyFRx6PKkmzv37xA6MPR9H",
                "TuJcLr9R9DrF1OHl3R7NPJs2zSZvthysnPK8dfEJ");

        // Enable the Parse push notification service for remote pushes.
        PushService.setDefaultPushCallback(this, MainActivity.class);

        // Setup the listener to handle local notifications for when talks start.
        Favorites.get().addListener(new FavoritesNotificationScheduler(this));

        // Read in the favorites from the local disk on this device.
        Favorites.get().findLocally(this);
    }
}
