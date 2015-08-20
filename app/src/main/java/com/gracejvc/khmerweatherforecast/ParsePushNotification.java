package com.gracejvc.khmerweatherforecast;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;

/**
 * Created by Chhea Vanrin on 8/20/2015.
 */
public class ParsePushNotification extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "eJAZaxYd0V2JclyfWw799Akukzz6CAHAGn01ruZ0", "F7OvrmzuZzMh0tJKvvTJ8I1141UjgOBPRbRQuRSv");
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

}
