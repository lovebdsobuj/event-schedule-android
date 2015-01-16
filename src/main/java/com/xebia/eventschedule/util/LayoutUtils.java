package com.xebia.eventschedule.util;

import android.content.Context;
import android.util.DisplayMetrics;

public class LayoutUtils {

    /**
     * @param context Context for accessing display metrics.
     * @return {@code true} if the main view contains two panes for master-detail mode.
     */
    public static boolean isDualPane(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        // master detail mode activated for screen width > 720dp
        return metrics.widthPixels / metrics.density > 720;
    }
}
