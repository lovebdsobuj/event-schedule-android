package com.xebia.xebicon2014;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.PushService;
import com.xebia.xebicon2014.model.Favorites;
import com.xebia.xebicon2014.model.Room;
import com.xebia.xebicon2014.model.Slot;
import com.xebia.xebicon2014.model.Speaker;
import com.xebia.xebicon2014.model.Talk;
import com.xebia.xebicon2014.util.FavoritesNotificationScheduler;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * The main app class mostly handles setting up global state, such as Parse keys.
 */
public class XebiConApp extends Application {

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
        Parse.initialize(this, "egcq81G2Yxf1C8yhAFtW5aOf7UkO5BsNB2mOyj2t",
                "skKIoBIE59IwNSzN6q1vrFRVn72TofyIkVGNlHLp");

        // Enable the Parse push notification service for remote pushes.
        PushService.setDefaultPushCallback(this, MainActivity.class);

        // Setup the listener to handle local notifications for when talks start.
        Favorites.get().addListener(new FavoritesNotificationScheduler(this));

        // Read in the favorites from the local disk on this device.
        Favorites.get().findLocally(this);
    }
}
