package com.gracejvc.khmerweatherforecast.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Chhea Vanrin on 7/22/2015.
 */
public class CamWeatherAuthenticatorService extends Service{
    CamWeatherAuthenticator mAuthenticator;
    @Override
    public void onCreate() {
        mAuthenticator = new CamWeatherAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
