package com.xebia.eventschedule.model;

import android.support.annotation.Nullable;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

/**
 * Model for the location where the event is held.
 */
@ParseClassName("Location")
public class Location extends ParseObject {

    @Nullable
    public String getAddress() {
        return getString("address");
    }

    @Nullable
    public ParseGeoPoint getCoords() {
        return getParseGeoPoint("coords");
    }

    @Nullable
    public String getDetails() {
        return getString("details");
    }

    @Nullable
    public String getDescription() {
        return getString("locationDescription");
    }

    @Nullable
    public String getName() {
        return getString("name");
    }

    @Nullable
    public String getUrl() {
        return getString("url");
    }
}
