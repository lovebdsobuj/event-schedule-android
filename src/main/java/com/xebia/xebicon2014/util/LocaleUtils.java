package com.xebia.xebicon2014.util;

import java.util.Locale;

/**
 * Created by steven on 25-4-14.
 */
public class LocaleUtils {

    public static boolean isDutch() {
        return "nld".equals(Locale.getDefault().getISO3Language());
    }
}
