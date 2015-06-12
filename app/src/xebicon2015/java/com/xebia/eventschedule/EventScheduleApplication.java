package com.xebia.eventschedule;

import com.xebia.eventschedule.util.BaseEventScheduleApp;
import com.xebia.eventschedule.util.FavoritesNotificationScheduler;
import com.xebia.eventschedule.util.ParsePushManager;

public class EventScheduleApplication extends BaseEventScheduleApp {

    @Override
    public void onCreate() {
        super.onCreate();
        ParsePushManager.registerForPushNotifications(FavoritesNotificationScheduler.isNotificationsEnabled(this));
    }

    @Override
    protected String getParseApplicationId() {
        return "0ykqcwEDNSAzXQGw8rpmAw8zzMKEDuLIz2Sj31fu";
    }
    //egcq81G2Yxf1C8yhAFtW5aOf7UkO5BsNB2mOyj2t

    @Override
    protected String getParseClientKey() {
        return "8XvCbouEfWFF775SYuTo28MxYvOxQoA4HeADScFG";
        //skKIoBIE59IwNSzN6q1vrFRVn72TofyIkVGNlHLp
    }

    @Override
    public String getParseEventId() {
        return "SykH4MNwTw";
    }


}
