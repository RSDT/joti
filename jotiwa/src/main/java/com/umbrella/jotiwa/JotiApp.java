package com.umbrella.jotiwa;

import android.app.Application;
import android.content.Context;

public class JotiApp extends Application {
    private static JotiApp instance;

    public static JotiApp getInstance() {
        return instance;
    }

    public static Context getContext(){
        return instance;
        // or return instance.getApplicationContext();
    }

    public static void toast()
    {

    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }
}