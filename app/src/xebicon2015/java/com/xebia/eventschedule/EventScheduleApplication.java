package com.xebia.eventschedule;

import android.util.Log;

import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.SaveCallback;
import com.xebia.eventschedule.util.BaseEventScheduleApp;

public class EventScheduleApplication extends BaseEventScheduleApp {

    @Override
    public void onCreate() {
        super.onCreate();
        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });
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
