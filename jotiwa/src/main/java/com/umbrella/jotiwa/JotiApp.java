package com.umbrella.jotiwa;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class JotiApp extends Application {
    private static JotiApp instance;
    private static Location lastloc;
    private static String noUsername = "unknown";

    public static void setLastLocation(Location loc){
        lastloc=loc;
    }
    public static Location getLastLocation(){
        return lastloc;
    }
    public static JotiApp getInstance() {
        return instance;
    }

    public static Context getContext() {
        return instance;
        //return instance.getApplicationContext();
    }

    public static void toast(CharSequence text) {
        Context context = JotiApp.getContext().getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public static void debug(CharSequence text) {
        SharedPreferences sharedpeferences = PreferenceManager.getDefaultSharedPreferences(JotiApp.getContext());
        boolean debug_on = sharedpeferences.getBoolean("pref_debug", false);
        if (debug_on) {
            JotiApp.toast(text);
        }
    }
    private static void toast(String text, int duration) {
        Context context = instance.getApplicationContext();
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public static String getNoUsername() {

        return noUsername;
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }
}