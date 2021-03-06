package com.xebia.eventschedule.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.xebia.eventschedule.BuildConfig;
import com.xebia.eventschedule.model.Favorites;
import com.xebia.eventschedule.model.Talk;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Listens for changes to the set of favorite talks and sets up alarms to send out notifications a
 * few minutes before the talks start.
 */
public class FavoritesNotificationScheduler implements Favorites.Listener {
    private static final String NOTIFICATION_PREFS = "notifications_" + BuildConfig.FLAVOR;
    private static final String NOTIFICATIONS_ENABLED = "notificationsEnabled";
    private final Context context;

  public FavoritesNotificationScheduler(Context context) {
    this.context = context;
  }

  /**
   * Creates a PendingIntent to be sent when the alarm for this talk goes off.
   */
  private PendingIntent getPendingIntent(Talk talk) {
    Intent intent = new Intent();
    intent.setClass(context, FavoritesNotificationReceiver.class);
    intent.setData(talk.getUri());
    return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
  }

  /**
   * Schedules an alarm to go off a few minutes before this talk.
   */
  private void scheduleNotification(Talk talk) {
    // We need to know the time slot of the talk, so fetch its data if we haven't already.
    if (!talk.isDataAvailable()) {
      Talk.getInBackground(talk.getObjectId(), new GetCallback<Talk>() {
        @Override
        public void done(Talk talk, ParseException e) {
          if (talk != null) {
            scheduleNotification(talk);
          }
        }
      });
      return;
    }

    // Figure out what time we need to set the alarm for.
    Date talkStart = null != talk.getSlot() ? talk.getSlot().getStartTime() : null;
    if (BuildConfig.DEBUG) {
      Logger.getLogger(getClass().getName()).log(Level.INFO, "Registering alarm for " + talkStart);
    }
    long fiveMinutesBefore = null != talkStart ? talkStart.getTime() - (5000 * 60) : Long.MAX_VALUE;
    if (fiveMinutesBefore < System.currentTimeMillis()) {
      return;
    }

    // Register the actual alarm.
    AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    PendingIntent pendingIntent = getPendingIntent(talk);
    manager.set(AlarmManager.RTC_WAKEUP, fiveMinutesBefore, pendingIntent);
  }

  /**
   * Cancels any alarm scheduled for the given talk.
   */
  private void unscheduleNotification(Talk talk) {
    AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    PendingIntent pendingIntent = getPendingIntent(talk);
    manager.cancel(pendingIntent);
  }

  @Override
  public void onFavoriteAdded(Talk talk) {
    scheduleNotification(talk);
  }

  @Override
  public void onFavoriteRemoved(Talk talk) {
    unscheduleNotification(talk);
  }

    public static void setNotificationsEnabled(final Context context, final boolean enabled) {
        final SharedPreferences prefs = context.getSharedPreferences(NOTIFICATION_PREFS, 0);
        prefs.edit().putBoolean(NOTIFICATIONS_ENABLED, enabled).apply();
    }

    public static boolean isNotificationsEnabled(final Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(NOTIFICATION_PREFS, 0);
        return prefs.getBoolean(NOTIFICATIONS_ENABLED, true);
    }
}
