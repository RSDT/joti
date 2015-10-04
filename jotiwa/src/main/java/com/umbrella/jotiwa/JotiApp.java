package com.umbrella.jotiwa;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

public class JotiApp extends Application {
    private static JotiApp instance;

    public static JotiApp getInstance() {
        return instance;
    }

    public static Context getContext() {
        return instance;
        // or return instance.getApplicationContext();
    }

    private void toast(String text) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private void toast(String text, int duration) {
        Context context = getApplicationContext();
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }
}