# XebiCon app #

This is the Android source code of the official [XebiCon](http://www.xebicon.com) apps. 

## Android Quick Setup ##

The Android XebiCon is already configured with the actual app keys required to connect to its Parse.com backend. It uses the Android Gradle plugin to build, so you need to install [Gradle](http://www.gradle.org/downloads) and set up your `GRADLE_HOME` and `ANDROID_HOME` environment variables. After that, run `gradle tasks` to get a list of executable tasks.

The project directory can be opened in [Android Studio](http://developer.android.com/sdk/installing/studio.html), which will automatically pick up the project structure through Gradle.

## Using Your Own Conference Data ##

As mentioned earlier, both versions of the XebiCon app are already configured to use the same Parse app as the App Store and Google Play versions of the XebiCon app. This is great if you just want to build and run the app and learn how it is all set up with Parse. However, if you're interested in reusing the codebase for your own conference app, you will need to create a new Parse app. For simplicity, we've included a JSON export with sample data which app which you can use for an initial import of data into your own app.

1. Go to your [Dashboard](https://parse.com/apps) and create a new Parse app.
2. Write down your new application id and client key. You will need these later. Remember that you can always get your keys from your app's Settings page.
3. Locate the `data` folder in your local clone of the PDD repo. Here you will find `Talk.json`, `Speaker.json`, `Slot.json`, and `Room.json`. These can be imported into your brand new Parse app.
4. Go to your app's Data Browser, and click on the "Import" button. Choose `Talk.json` and give your new class the name "Talk". Repeat this for each of the json files in the `data` folder, giving them the appropriate class name.
5. Set up your app keys in your project (see "Custom App Setup" below).
6. Build and Run.

Confirm that everything is working correctly. You may now modify the list of Talks, Speakers, and Rooms to suit your conference.

### Android Custom App Setup ###

Open the `ParseDevDayApp.java` file and modify the following two lines to use your actual Parse application id and client key, as noted in step 2 above:

```
Parse.initialize(this, "YOUR_APPLICATION_ID",
  "YOUR_CLIENT_KEY");
```
