package com.xebia.eventschedule.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.xebia.eventschedule.util.LocaleUtils;

/**
 * A person speaking in a talk.
 */
@ParseClassName("Speaker")
public class Speaker extends ParseObject {
  public String getName() {
    return getString("name");
  }

  public String getTitle() {
    return getString(LocaleUtils.isDutch() ? "title_nl" : "title");
  }

  public String getCompany() {
    return getString("company");
  }

  public String getBio() {
    return getString(LocaleUtils.isDutch() ? "bio_nl" : "bio");
  }
  
  public String getPhotoURL() {
    return getString("photoURL");
  }

  public ParseFile getPhoto() {
    return getParseFile("photo");
  }

  public String getTwitter() {
    return getString("twitter");
  }
}
