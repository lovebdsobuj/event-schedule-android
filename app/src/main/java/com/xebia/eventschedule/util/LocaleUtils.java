package com.xebia.eventschedule.util;

import com.xebia.eventschedule.model.Event;

import java.util.Locale;

/**
 * Helper for localisation tasks
 *
 * Created by steven on 25-4-14.
 */
public class LocaleUtils {

    private static boolean isDutch() {
        return "nld".equals(Locale.getDefault().getISO3Language());
    }

    /**
     * Tries to the match the phone's locale to the languages that the event supports. If there is no match,
     * the default label is "en".
     *
     * @param event The event
     * @return A label that can be used to get localised resources from the event data set
     */
    public static String getLocaleLabel(final Event event) {
        return isDutch() && null != event && event.getLanguages().contains("nl") ? "nl" : "en";
    }
}
