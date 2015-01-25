package com.xebia.eventschedule.model;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

@ParseClassName("Location")
public class Location extends ParseObject {

    public String getAddress() {
        return getString("address");
    }

    public ParseGeoPoint getCoords() {
        return getParseGeoPoint("coords");
    }

    public String getDetails() {
        return getString("details");
    }

    public String getDescription() {
        return getString("locationDescription");
    }

    public String getName() {
        return getString("name");
    }

    public String getUrl() {
        return getString("url");
    }
}
