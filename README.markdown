Xebia Event Schedule app for Android
====================================

This is the Android source code of the Xebia Event Schedule app, formerly the XebiCon app.

Please note that this app is based off of the [Parse.com Dev Day App](https://github
.com/ParsePlatform/ParseDeveloperDay). Since that code was made available under a CC0 license it is not strictly
required to do any attribution, but it is still the proper thing to do. Also all code in these repositories is made
available under a CC0 license as well. Do note that all image images clearly branding the name "XebiCon" are still
copyrighted by Xebia B.V. Netherlands.

## Using Your Own Conference Data

As mentioned earlier, both versions of the Xebia Event Schedule app are already configured to use the same Parse app as
the App Store and Google Play versions of the XebiCon app. This is great if you just want to build and run the app and
learn how it is all set up with Parse. However, if you are interested in reusing the codebase for your own conference
app, you will need to create a new Parse app. For simplicity, we have included a JSON export with sample data which app
which you can use for an initial import of data into your own app.

1. Go to your [Dashboard](https://parse.com/apps) and create a new Parse app.
2. Write down your new application id and client key. You will need these later. Remember that you can always get your
   keys from your app's Settings page.
3. Locate the `data` folder in your local clone of the PDD repo. Here you will find `Talk.json`, `Speaker.json`,
   `Slot.json`, and `Room.json`. These can be imported into your brand new Parse app.
4. Go to your app's Data Browser, and click on the "Import" button. Choose `Talk.json` and give your new class the name
   "Talk". Repeat this for each of the JSON files in the `data` folder, giving them the appropriate class name.
5. Set up your app keys in your project (see "Custom App Setup" below).
6. Build and Run.

Confirm that everything is working correctly. You may now modify the list of Talks, Speakers, and Rooms to suit your
conference.

### Android Custom App Setup

Open the `EventScheduleApp.java` file and modify the following two lines to use your actual Parse application id and
client key, as noted in step 2 above:

```java
Parse.initialize(this, "YOUR_APPLICATION_ID", "YOUR_CLIENT_KEY");
```
### Customizing your notifications

When a user marks a talk as a favorite, then `FavoritesNotificationScheduler`'s `onFavoriteAdded` is called, which in turn
uses the `FavoritesNotificationReceiver` to schedule a notification via the NotificationManager.
You can override the NotificationManager to fully customize the notification. You can now use the styles.xml,
colors.xml and attrs.xml to override the color and the backgroundcolor of the notificaiton icon.
