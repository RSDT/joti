package com.umbrella.joti;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;

public class LocationHandler extends Service {
    public LocationHandler() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
    // TODO: Return the communication channel to the service.
         throw new UnsupportedOperationException("Not yet implemented");
    }
}
