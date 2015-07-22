package com.gracejvc.khmerweatherforecast.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Chhea Vanrin on 7/22/2015.
 */
public class CamWeatherSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private  static CamWeatherSyncAdapter sCamWeatherSynceAdapter = null;
    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock){
            if (sCamWeatherSynceAdapter==null){
                sCamWeatherSynceAdapter = new CamWeatherSyncAdapter(getApplicationContext(),true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sCamWeatherSynceAdapter.getSyncAdapterBinder();
    }
}
