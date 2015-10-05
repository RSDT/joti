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
        return instance.getApplicationContext();
    }

    public static void toast(CharSequence text) {
        Context context = JotiApp.getContext().getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private static void toast(String text, int duration) {
        Context context = instance.getApplicationContext();
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }
}