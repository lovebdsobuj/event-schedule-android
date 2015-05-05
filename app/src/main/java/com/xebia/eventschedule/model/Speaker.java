package com.xebia.eventschedule.model;

import android.support.annotation.Nullable;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

/**
 * A person speaking in a talk.
 */
@ParseClassName("Speaker")
public class Speaker extends ParseObject {

    @Nullable
    public String getName() {
        return getString("name");
    }

    @Nullable
    public String getTitle() {
        return getString("title");
    }

    @Nullable
    public String getCompany() {
        return getString("company");
    }

    @Nullable
    public String getBio() {
        return getString("bio");
    }

    @Nullable
    public String getPhotoURL() {
        ParseFile photoFile = getPhoto();
        if (photoFile == null) {
            return null;
        }
        return photoFile.getUrl();
    }

    @Nullable
    public ParseFile getPhoto() {
        return getParseFile("photo");
    }

    @Nullable
    public String getTwitter() {
        return getString("twitter");
    }

    @Override
    public String toString() {
        return null != getName() ? getName() : "Unknown Speaker";
    }
}
