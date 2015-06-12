package com.xebia.eventschedule.util;

import android.util.Log;

import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.SaveCallback;

/**
 * manager to handle parse push registration
 */
public class ParsePushManager {
    private static final String TAG = "com.parse.push";
    private static void registerForPushNotificationsInBackground(){
        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d(TAG, "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e(TAG, "failed to subscribe for push", e);
                }
            }
        });
    }

    private static void unregisterForPushNotificationsInBackground(){
        ParsePush.unsubscribeInBackground("", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d(TAG, "successfully unsubscribed to the broadcast channel.");
                } else {
                    Log.e(TAG, "failed to unsubscribed for push", e);
                }
            }
        });
    }

    public static void registerForPushNotifications(boolean shouldRegister){
        if(shouldRegister){
            registerForPushNotificationsInBackground();
        }else{
            unregisterForPushNotificationsInBackground();
        }
    }
}
